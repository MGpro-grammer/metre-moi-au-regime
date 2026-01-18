module metre.moi.au.regime {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    requires tess4j;
    requires org.slf4j;

    opens be.esi.prj to javafx.fxml;
    opens be.esi.prj.controller to javafx.fxml;

    exports be.esi.prj;
    exports be.esi.prj.controller;
    exports be.esi.prj.dto;
    exports be.esi.prj.viewmodel;
    exports be.esi.prj.service;
    exports be.esi.prj.model;
    exports be.esi.prj.repository;
    exports be.esi.prj.enumeration;
}