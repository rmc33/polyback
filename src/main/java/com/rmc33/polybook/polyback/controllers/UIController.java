

package com.rmc33.polybook.polyback.controllers;

import com.google.common.io.Resources;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import com.rmc33.polybook.polyback.service.FirestoreSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/ui")
public class UIController  {
    private static final Logger logger = Logger.getLogger(UserDataController.class.getName());

    @GetMapping(value="/privacy", produces = "text/html")
    public String getPrivacyRequest() {
        SoyFileSet sfs = SoyFileSet.builder().add(Resources.getResource("privacy.soy")).build();
        SoyTofu tofu = sfs.compileToTofu();
        return tofu.newRenderer("com.rmc33.polyback.privacy.main").render();
    }

    @GetMapping(value="/support", produces = "text/html")
    public String getSupportRequest() {
        SoyFileSet sfs = SoyFileSet.builder().add(Resources.getResource("support.soy")).build();
        SoyTofu tofu = sfs.compileToTofu();
        return tofu.newRenderer("com.rmc33.polyback.support.main").render();
    }
}