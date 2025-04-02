package org.example.model;

public class Artist {
    private String id;
    private String name;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Artist{id='" + id + "', name='" + name + "'}";
    }
}
