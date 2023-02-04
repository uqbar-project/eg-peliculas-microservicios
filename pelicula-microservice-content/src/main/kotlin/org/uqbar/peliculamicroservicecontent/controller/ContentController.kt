package org.uqbar.peliculamicroservicecontent.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.uqbar.peliculamicroservicecontent.service.ContentService

@RestController
@CrossOrigin(origins = ["**"])
@RequestMapping("/content")
class ContentController {

    @Autowired
    lateinit var contentService: ContentService

    @PatchMapping("/watch")
    fun watchContent(@RequestBody idContent: String) = contentService.watchContent(idContent, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3NTUxMDMxMywiZXhwIjoxNjc2NTkwMzEzLCJyb2xlcyI6IlJPTEVfQURNSU4ifQ.s50WRZLD_xlGsByjxZi4LQocuFEVUpRrSSdtqjE6_bqrvkOsU4V9uqBZW3uzkg7PS58hACXwmcElsEscH3K7kQ")
}