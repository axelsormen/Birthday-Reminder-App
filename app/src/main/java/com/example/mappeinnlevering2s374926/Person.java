package com.example.mappeinnlevering2s374926;

public class Person {
    private long id;
    private String navn;
    private String telefonnummer;
    private String foedselsdato;

    public Person(long id, String navn, String telefonnummer, String foedselsdato) {
        this.id = id;
        this.navn = navn;
        this.telefonnummer = telefonnummer;
        this.foedselsdato = foedselsdato;
    }

    public long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public String getFoedselsdato() {
        return foedselsdato;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public void setFoedselsdato(String foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public Person() {
    }

    public Person(long id) {
        this.id = id;
    }
}