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
    fun watchContent(@RequestBody idContent: String) = contentService.watchContent(idContent)
}