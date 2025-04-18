package com.pack.uniflow.DummyData;

import java.util.List;

public class DummyUniversity {
    private String name;
    private List<DummyStudent> students;

    public DummyUniversity(String name, List<DummyStudent> students) {
        this.name = name;
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public List<DummyStudent> getStudents() {
        return students;
    }
}

