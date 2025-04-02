package org.example.model;

public class Artist {
    private String name;
    private String genre;
    private String bio;

    public Artist(String name, String genre, String bio) {
        this.name = name;
        this.genre = genre;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getBio() {
        return bio;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "🎤 Artista: " + name + "\n" +
                "🎶 Género: " + genre + "\n" +
                "📝 Biografía: " + (bio != null ? bio : "No disponible");
    }
}

