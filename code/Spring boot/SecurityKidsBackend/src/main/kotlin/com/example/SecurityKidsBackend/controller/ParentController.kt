package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.Parent
import com.example.SecurityKidsBackend.data.service.ParentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping(path = ["/parents"])
class ParentController(
    @Autowired
    val service: ParentService
) {

    @GetMapping
    fun getAllParent(): List<Parent> = service.getAllParent()

    @GetMapping(path = ["{parentId}"])
    fun getParent(@PathVariable("parentId") parentId: Long): Parent{
        return service.getParent(parentId)
    }

    @GetMapping(path = ["/name"])
    fun getAuthenticatedParent(
        @RequestParam name: String,
        @RequestParam psw: String):Parent{
        return service.getParentByNameAndPassword(name, psw)
    }

    @GetMapping(path = ["child/{childId}"])
    fun getParentChild(@PathVariable("childId") childId: Long) = service.getParentChild(childId)

    @GetMapping(path = ["image/{parentId}"])
    fun getParentImage(@PathVariable parentId: Long): ResponseEntity<Resource>{
        val image = service.getParentImage(parentId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.type))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.name + "\""
            )
            .body(ByteArrayResource(image.data))

    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun insertParent(@RequestPart("parent") parent: Parent,@RequestPart("image") image: MultipartFile)
    : ResponseEntity<String>{
        return try {
            service.addNewParent(parent,image)
            val message = "file uploaded successfully ${image.originalFilename}"
            ResponseEntity.status(HttpStatus.OK).body(message)
        }catch (e: Exception){
            val message = "file could not upload ${image.originalFilename} error ${e.message} !"
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message)
        }

    }

    @PutMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateParent(@RequestPart("parent") parent: Parent,@RequestPart("image") image: MultipartFile)
    :ResponseEntity<String>{
        return try {
            service.updateParent(parent,image)
            val message = "file uploaded successfully ${image.originalFilename}"
            ResponseEntity.status(HttpStatus.OK).body(message)
        }catch (e: Exception){
            val message = "file could not upload ${image.originalFilename} error ${e.message} !"
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message)
        }

    }

    @DeleteMapping(path = ["{prentId}"])
    fun deleteParent(@PathVariable("prentId") parentId: Long){
        service.deleteParent(parentId)
    }
}