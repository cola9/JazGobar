package com.example;
import java.util.UUID;
/**
 * Created by nikolajcolic on 25/02/17.
 */

public class Goba {
    private String id;
    private String ime;
    private boolean checked;

    public Goba(Goba t) {
        ime =t.getIme();
        checked = t.isChecked();
    }
    public String getHtmlFromat() {
        if (checked) {
            return "<b>"+ime+"<b>";
        } else {
            return ime;
        }
    }
    public Goba(String ime) {
        this.id=UUID.randomUUID().toString().replaceAll("-", "");
        this.ime = ime;
    }

    @Override
    public String toString() {
        return "Goba{" +
                "id='" + id + '\'' +
                ", ime='" + ime + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
