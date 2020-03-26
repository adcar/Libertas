package dev.acardosi.libertas;

import org.jetbrains.annotations.NotNull;

public class Vote {
    private String id;
    private boolean isUpvoted;
    private boolean isDownvoted;

    Vote(String id) {
        this.id = id;
        this.isDownvoted = false;
        this.isUpvoted = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    boolean isUpvoted() {
        return isUpvoted;
    }
    boolean isDownvoted() {return isDownvoted; }

    void setUpvoted(boolean upvoted) {
        isUpvoted = upvoted;
    }

    void setDownvoted(boolean downvoted) {
        isDownvoted = downvoted;
    }


    @NotNull
    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", isUpvoted=" + isUpvoted +
                ", isDownvoted=" + isDownvoted +
                '}';
    }
}
