package model;

import java.util.List;

public class CodeResponse {

    private List<String> bugs;
    private List<String> improvements;
    private int score;
    private String summary;
    private String fixed_code;

    public List<String> getBugs() { return bugs; }
    public void setBugs(List<String> bugs) { this.bugs = bugs; }

    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getFixed_code() { return fixed_code; }
    public void setFixed_code(String fixed_code) { this.fixed_code = fixed_code; }
}
