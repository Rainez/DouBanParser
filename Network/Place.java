package Network;

public class Place {
    private String classRoom;

    public Place(String classRoom, String campus) {
        this.classRoom = classRoom;
        this.campus = campus;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    private String campus;

}
