package com.libertymutual.goforcode.sparkapp.utilities;

import java.io.Closeable;

import org.javalite.activejdbc.Base;

public class AutoCloseableDB implements Closeable, AutoCloseable {

    public AutoCloseableDB() {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/rental", "rental", "rental");
    }

    @Override
    public void close() {
        Base.close();
    }

}
