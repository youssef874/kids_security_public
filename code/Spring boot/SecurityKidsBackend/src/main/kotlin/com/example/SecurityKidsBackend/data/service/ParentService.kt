package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Image
import com.example.SecurityKidsBackend.data.entities.Parent
import com.example.SecurityKidsBackend.data.repository.ParentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.util.Optional
import javax.transaction.Transactional

@Service
class ParentService(
    @Autowired
    val repository: ParentRepository
) {

    fun getParent(id: Long) = repository.findById(id).get()

    fun getAllParent(): List<Parent> = repository.findAll()

    fun getParentImage(parentId: Long): Image{
        val parent = repository.findById(parentId)
        return Image(
            name = parent.get().imageParent,
            type = parent.get().imageType,
            data = parent.get().data
        )
    }

    fun getParentChild(childId: Long): Parent{
        val p = repository.getParentChild(childId)
        if (p.isPresent)
            return p.get()
        return Parent()
    }

    fun getParentByNameAndPassword(name: String, psw: String) = repository.getParentByLoginAndPassword(name, psw).get()

    fun addNewParent(parent: Parent, image: MultipartFile): Parent {
        val parentByPhoneNumber: Optional<Parent> = repository.getParentByTel(parent.telParent)
        val parentByLogin : Optional<Parent> = repository.getParentByLogin(parent.loginParent)
        val fileName = image.originalFilename?.let { StringUtils.cleanPath(it) }
        try {
            if (!parentByPhoneNumber.isPresent && ! parentByLogin.isPresent) {
                if (fileName != null) {
                    if (fileName.contains(".."))
                        throw Exception("Invalid path sequence")
                    if (image.contentType.isNullOrEmpty())
                        throw Exception("Invalid content type")
                    parent.data = image.bytes
                    parent.imageParent = fileName
                    parent.imageType =image.contentType!!
                }
                return repository.save(parent)
            }
        }catch (_: Exception){

        }
        return Parent()
    }

    @Transactional
    fun updateParent(parent: Parent, image: MultipartFile)  {
        val pOptional = repository.getParentByLogin(parent.loginParent)
        val fileName = image.originalFilename?.let { StringUtils.cleanPath(it) }
        try {
            val p = pOptional.get()
            if (pOptional.isPresent) {
                p.updateName(parent.nameParent)
                p.updatePhoneNumber(parent.telParent)
                p.updatePhoneNumber(parent.telParent)
                if (fileName != null) {
                    if (fileName.contains(".."))
                        throw Exception("Invalid path sequence")
                    if (image.contentType.isNullOrEmpty())
                        throw Exception("Invalid content type")
                    p.data = image.bytes
                    p.updateImageParent(fileName)
                    p.updateImageType(image.contentType)
                }
            }
        }catch (_:Exception){

        }
    }

    fun deleteParent(parentId: Long) {
        val p = repository.findById(parentId)
        if (p.isPresent) {
            repository.delete(p.get())
        }
    }

}