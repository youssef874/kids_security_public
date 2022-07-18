package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.service.ChildService
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
@RequestMapping(path = ["/children"])
class ChildController(
    @Autowired
    val service: ChildService
) {

    @GetMapping(path = ["{childId}"])
    fun getChildren(@PathVariable("childId") childId: Long): Child {
        return service.getChild(childId)
    }

    @GetMapping
    fun getAllChildren() = service.getAllChildren()

    @GetMapping(path = ["/name"])
    fun getChildByNameAndPassword(
        @RequestParam name: String,
        @RequestParam psw: String) = service.getChildByNameAndPassword(name, psw)

    @GetMapping(path = ["/image/{childId}"])
    fun getChildImage(@PathVariable childId: Long): ResponseEntity<Resource>{
        val image = service.getChildImage(childId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.type))
            .header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.name + "\""
            )
            .body(ByteArrayResource(image.data))
    }

    @PostMapping(path = ["{parentId}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun insertChild(@RequestPart("child") child: Child,@RequestPart("image")image: MultipartFile,
    @PathVariable parentId: Long):ResponseEntity<String> {
        return try {
            val c = service.addNewChild(child,image,parentId)
            print("child: $c")
            val message = "file upload successfully ${image.originalFilename}"
            ResponseEntity.status(HttpStatus.OK).body(message)
        }catch (e: Exception){
            val message = "file could not upload ${image.originalFilename} error: ${e.message} !"
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message)
        }
    }

    @DeleteMapping(path = ["{childId}"])
    fun deleteChild(@PathVariable("childId") childId: Long) {
        service.deleteChild(childId)
    }

    @DeleteMapping(path = ["/parent/{parentId}"])
    fun deleteParentChildren(@PathVariable("parentId") parentId: Long) {
        service.deleteAllParentChildren(parentId)
    }

    @PutMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateChild(@RequestPart("child") child: Child,@RequestPart("image")image: MultipartFile)
    : ResponseEntity<String> {
        return try {
            service.updateChild(child,image)
            val message = "file uploaded successfully ${image.originalFilename}"
            ResponseEntity.status(HttpStatus.OK).body(message)
        }catch (e: Exception){
            val message = "file could not upload ${image.originalFilename} error ${e.message} !"
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message)
        }
    }
}