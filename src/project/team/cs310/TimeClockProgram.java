package project.team.cs310;

//this is the main class
//I like coding
//github ignores me
public class TimeClockProgram {

    public static void main(String[] args) {
        // TODO code application logic here
        TASDatabase db = new TASDatabase();
        db.Connection();
        db.getBadge("08D01475");
    }
    
}
