package org.uqbar.peliculamicroservicecontent.graphql

import java.io.IOException


object GraphqlSchemaReaderUtil {
    @Throws(IOException::class)
    fun getSchemaFromFileName(filename: String) =
        (GraphqlSchemaReaderUtil::class.java).classLoader.getResourceAsStream("graphql/$filename.graphql")!!
                .readAllBytes().toString()


}