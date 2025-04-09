package org.example;

public class Event {
    private long ts;
    private String ss;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    @Override
    public String toString() {
        return "Event{" +
                "ts=" + ts +
                ", ss='" + ss + '\'' +
                '}';
    }
}
