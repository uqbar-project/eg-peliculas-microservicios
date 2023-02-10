package org.uqbar.peliculamicroservicecontent.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.uqbar.peliculamicroservicecontent.model.Content

@Repository
interface ContentRepository : CrudRepository<Content, String> {
}