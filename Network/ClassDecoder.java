package Network;

import org.htmlcleaner.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Network.ClassDecoder.ClassDefination.getAllClasses;
import static net.mindview.util.Print.print;

// 第一阶段：我先用类把数据表示出来
// 第二阶段  写入XML文件
// 命令行化
// 图形界面化
// @ver1.0
// 这个版本的课表不提供修改信息的功能
// 未来请提供关于过去课程的查询。
// 目前只专注于现在的课程
public class ClassDecoder {
    private static String name;
    private static String studentNumber;

    public static void main(String[] args) throws Exception {
       saveClassesToFileUsingTransforer();
    }

    public static Document turnClassToDocument() throws Exception {
        StringBuilder stringBuilder=new StringBuilder();
        List<ClassDefination> definations = getAllClasses();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("classDef");
        root.setAttribute("student_name", name);
        root.setAttribute("student_number", studentNumber);
        Element yearDuration=doc.createElement("yearduration");

        String yearD=definations.get(0).getYear();
        String[] pieces=yearD.split("-");
        root.appendChild(yearDuration);
        Element startYear=doc.createElement("start");
        startYear.setTextContent(pieces[0]);
        yearDuration.appendChild(startYear);
        Element endYear=doc.createElement("end");
        endYear.setTextContent(pieces[1]);
        yearDuration.appendChild(endYear);
        int counter = 0;
        for (ClassDefination classDefination : definations) {
            Element child = doc.createElement("class");
            root.appendChild(child);
            Class<?> cl = ClassDefination.class;
            String year = classDefination.year;
            String[] split = year.split("-");
            Element yearduration = doc.createElement("yearduration");
            child.appendChild(yearduration);
            Element start = doc.createElement("start");
            start.setTextContent(split[0]);
            yearduration.appendChild(start);
            Element end = doc.createElement("end");
            end.setTextContent(split[1]);
            yearduration.appendChild(end);
            Field f;
            for (int i = 0; i < ClassDefination.partDef.length; i++) {
                String def = ClassDefination.partDef[i];
                if (def.equals("time")) {
                    List<ClassSpcialDateTime> classSpcialDateTimeList = classDefination.getTime();
                    Element compact = doc.createElement("compact");
                    for (ClassSpcialDateTime timespcial : classSpcialDateTimeList) {
                        Element place = doc.createElement("Place");
                        place.setTextContent(timespcial.getPlace().getClassRoom());
                        compact.appendChild(place);
                        Element time = doc.createElement("time");
                        stringBuilder.append(timespcial.getStartSection());
                        stringBuilder.append("-");
                        stringBuilder.append(timespcial.getEndSection());
                        time.setTextContent(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        compact.appendChild(time);
                        child.appendChild(compact);
                    }
                }
                else {
                        Field field=cl.getDeclaredField(def);
                        field.setAccessible(true);
                        Element generateByField=doc.createElement(def);
                        String value=(String)(field.get(classDefination));
                        generateByField.setTextContent(value);
                        child.appendChild(generateByField);
                    }
                  }
            }

      doc.appendChild(root);
        return doc;

        }

     public static void saveClassesToFileUsingTransforer() throws Exception{
        Document doc=turnClassToDocument();
         Transformer transformer= TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"classDef.dtd");
         transformer.setOutputProperty(OutputKeys.INDENT,"yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
         transformer.setOutputProperty(OutputKeys.METHOD,"xml");
         Path path= Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/results.xml");
         transformer.transform(new DOMSource(doc),new StreamResult(Files.newOutputStream(path)));

     }

    static class ClassDefination {
        protected String year;
        protected String semester;
        protected String[] alldefs = new String[10];
        protected static String[] allFieledNames = new String[10];

        public String getSemester(){
            return semester;
        }
        public String getYear(){
            return year;
        }
        static {
            allFieledNames[0] = "year";
            allFieledNames[1] = "semester";
            allFieledNames[2] = "classNumber";
            allFieledNames[3] = "className";
            allFieledNames[4] = "classType";
            allFieledNames[5] = "credits";
            allFieledNames[6] = "cagotory";
            allFieledNames[7] = "time";
            allFieledNames[8] = "campus";
            allFieledNames[9] = "teacher";
        }

        public static String[] partDef = new String[9];

        static {
            for (int i = 0; i <= 8; i++) {
                partDef[i] = allFieledNames[i + 1];
            }
        }

        public ClassDefination(String year, String semester, String classNumber, String className, String classType, String credits, String cagotory, String time, String campus, String teacher) {
            this.year = year;
            alldefs[0] = year;
            this.semester = semester;
            alldefs[1] = semester;
            this.classNumber = classNumber;
            alldefs[2] = classNumber;
            this.className = className;
            alldefs[3] = className;
            this.classType = classType;
            alldefs[4] = classType;
            this.credits = credits;
            alldefs[5] = credits;
            this.cagotory = cagotory;
            alldefs[6] = cagotory;
            this.time = time;
            alldefs[7] = time;
            this.campus = campus;
            alldefs[8] = campus;
            this.teacher = teacher;
            alldefs[9] = teacher;
            this.campus=getCampus();
        }

        protected String classNumber;
        protected String className;
        protected String classType;
        protected String credits;
        protected String cagotory;
        protected String time;
        protected String campus;
        protected String teacher;

        public String getTeacher() {
            return teacher;
        }

        public String getCredits() {
            return credits;
        }

        public String getCagotory() {
            return cagotory;
        }

        public String getClassName() {
            return className;
        }

        public String getClassNumber() {
            return classNumber;
        }

        public String getClassType() {
            return classType;
        }

        public List<ClassSpcialDateTime> getTime() throws Exception {
            String[] timePieces = time.split(",");
            List<ClassSpcialDateTime> classSpcialDateTimeList=new ArrayList<>();
//            ClassSpcialDateTime[] spcialDateTimes=new ClassSpcialDateTime[timePieces.length];
            String pattern= "([\\S]{3})\\s+?(\\d{1,})-(\\d{1,})\\s+.*?/([\\S]{5,}).*?[(（]{1}(\\d{1,}).*?(\\d{1,})";
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = null;
            for (String test : timePieces) {
                if (matcher == null)
                    matcher = patt.matcher(test);
                else
                    matcher.reset(test);
                while (matcher.find()) {
                    DayOfWeek dayOfWeek=DaysOfWeekInChinese.ChineseWeekDayToUniversal(matcher.group(1));
                    int  startSection=Integer.parseInt(matcher.group(2));
                    int  endSection=Integer.parseInt(matcher.group(3));
                    String place_str=matcher.group(4);
                    String camp=getCampus();
                    Place place=new Place(place_str,camp);
                    int startWeek=Integer.parseInt(matcher.group(5));
                    int endWeek=Integer.parseInt(matcher.group(6));
                    ClassSpcialDateTime spcialDateTime=new ClassSpcialDateTime(startSection,endSection,startWeek,endWeek,dayOfWeek,place);
                    classSpcialDateTimeList.add(spcialDateTime);
                }
            }
            return classSpcialDateTimeList;
        }

        public String getCampus() {
            switch (campus) {
                case "东":
                    return "东校区";
                case "西":
                    return "西校区";
                case "南":
                    return "南校区";
                case "北":
                    return "北校区";
            }
            return null;
        }

           @Override
            public String toString() {
                return "ClassDefination{" +

                        "  year='" + year + '\'' +
                        ", semester='" + semester + '\'' +
                        ", className='" + className + '\'' +
                        ", classNumber='" + classNumber + '\'' +
                        ", classType='" + classType + '\'' +
                        ", credits='" + credits + '\'' +
                        ", cagotory='" + cagotory + '\'' +
                        ", time='" + time + '\'' +
                        ", campus='" + campus + '\'' +
                        ", teacher='" + teacher + '\'' +
                        '}';
            }


        public static List<ClassDefination> getAllClasses() throws Exception {
            HtmlCleaner cleaner = new HtmlCleaner();
            File classFile = new File("/Users/mac/Desktop/classes.html");
            TagNode root = cleaner.clean(classFile);

            Object[] nodesets = root.evaluateXPath("//body/div[2]/table/tbody/tr/td[2]");
            name = ((TagNode) nodesets[0]).getText().toString();
//        print(name);
            nodesets = root.evaluateXPath("//body/div[2]/table/tbody/tr/td[4]");
            studentNumber = ((TagNode) nodesets[0]).getText().toString();
//        print(studentNumber);
            nodesets = root.evaluateXPath("//*[@id=\"content\"]/h3");
            String summarize = ((TagNode) nodesets[0]).getText().toString();
            nodesets = root.evaluateXPath("//*[@id=\"elected\"]/tbody//tr");


            List<ClassDefination> list = new ArrayList<>();
            for (int i = 0; i < nodesets.length; i++) {
                TagNode tableBody = ((TagNode) nodesets[i]);
                tableBody.traverse(new TagNodeVisitor() {
                    String year = null;
                    String semester = null;
                    String classNumber = null;
                    String className = null;
                    String classType = null;
                    String credits = null;
                    String cagotory = null;
                    String time = null;
                    String campus = null;
                    String teacher = null;
                    boolean skip = true;
                    int counter = 0;

                    @Override
                    public boolean visit(TagNode tagNode, HtmlNode htmlNode) {

                        if (htmlNode instanceof ContentNode) {
                            String contents = ((ContentNode) htmlNode).getContent().trim();
                            if (contents == null || contents.length() == 0)
                                contents = "empty";
//                      print(contents);
                            if (counter >= 10) {
                                if (skip) {
                                    skip = false;
                                    return true;
                                } else if (!skip) {
                                    skip = true;
                                }
                            }
                            if (!contents.equals("empty") || (contents.equals("empty") && counter >= 10)) {
                                // print(contents);
                                counter++;
                                switch (counter) {
                                    case 1:
                                        year = contents;
                                        break;
                                    case 2:
                                        semester = contents;
                                        break;
                                    case 3:
                                        classNumber = contents;
                                        break;
                                    case 4:
                                        className = contents;
                                        break;
                                    case 5:
                                        classType = contents;
                                        break;
                                    case 6:
                                        credits = contents;
                                        break;
                                    case 7:
                                        cagotory = contents;
                                        break;

                                    case 8:
                                        time = contents;
                                        break;
                                    case 9:
                                        campus = contents;
                                        break;
                                    case 10:
                                        teacher = contents;
                                        list.add(new ClassDefination(year, semester, classNumber, className, classType, credits, cagotory, time, campus, teacher));
                                        break;
                                    case 11:
                                        break;
                                    case 12:
                                        break;
                                    case 13:
                                        break;
                                    case 14:
                                        counter = 0;
                                        break;
                                }
                            }


                        }
                        return true;
                    }
                });
//        print(list);
            }
            return list;

        }
    }
}
