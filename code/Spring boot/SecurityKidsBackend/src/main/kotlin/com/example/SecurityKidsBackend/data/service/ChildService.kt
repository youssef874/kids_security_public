package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.Image
import com.example.SecurityKidsBackend.data.entities.Parent
import com.example.SecurityKidsBackend.data.repository.ChildRepository
import com.example.SecurityKidsBackend.data.repository.ParentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.transaction.Transactional

@Service
class ChildService(
    @Autowired
    val repository: ChildRepository,
    @Autowired
    val parentRepository: ParentRepository
) {

    fun getChild(childId: Long): Child = repository.findById(childId).get()

    fun getAllChildren(): MutableList<Child> = repository.findAll()

    fun getChildByNameAndPassword(name: String,psw: String) = repository.getChildLoginAndPassword(name, psw)

    fun getChildImage(childId: Long): Image{
        val child = repository.findById(childId)
        if (!child.isPresent)
            throw Exception("Child Not existed")
        return Image(
            name = child.get().imageChild,
            type = child.get().imageType,
            data = child.get().data
        )
    }

    fun addNewChild(child: Child, image: MultipartFile, parentId: Long): Child{
        val childByPhoneNumber: Optional<Child> = repository.getChildByTel(child.telChild)
        val parentOptional: Optional<Parent> = parentRepository.findById(child.parent.idParent)
        val childByLogin: Optional<Child> = repository.getChildByLogin(child.loginChild)
        try {
            if (!childByPhoneNumber.isPresent && !parentOptional.isPresent && !childByLogin.isPresent){
                val imageName = image.originalFilename?.let { StringUtils.cleanPath(it) }
                if (imageName != null){
                    if (imageName.contains(".."))
                        throw Exception("Invalid path sequence")
                    if (image.contentType.isNullOrEmpty())
                        throw Exception("Invalid content type")
                    child.imageChild = imageName
                    child.data = image.bytes
                    child.imageType = image.contentType.toString()
                }
                val c = Child(nameChild = child.nameChild, telChild = child.telChild, loginChild = child.loginChild
                    , pswChild = child.pswChild, parent = Parent(idParent = parentId)
                )
                print("$c")
                return repository.save(
                    Child(nameChild = child.nameChild, telChild = child.telChild, loginChild = child.loginChild
                        , pswChild = child.pswChild, parent = Parent(idParent = parentId), data = child.data,
                        imageType = child.imageType, imageChild = child.imageChild
                    )
                )
            }
        }catch (_: Exception){

        }
        return Child()
    }

    @Transactional
    fun updateChild(child: Child,image: MultipartFile){
        val cOptional: Optional<Child> = repository.getChildByLogin(child.loginChild)
        val imageName = image.originalFilename?.let { StringUtils.cleanPath(it) }
        try {
            if (cOptional.isPresent){
                if (imageName != null){
                    cOptional.get().updateLoginChild(child.loginChild)
                    cOptional.get().updateNameChild(child.nameChild)
                    cOptional.get().updatePhoneNumber(child.telChild)
                    if (imageName.contains(".."))
                        throw Exception("Invalid path sequence")
                    if (image.contentType.isNullOrEmpty())
                        throw Exception("Invalid content type")
                    cOptional.get().updateImageChild(imageName)
                    cOptional.get().updateData(image.bytes)
                    cOptional.get().updateImageType(image.contentType)
                }
            }
        }catch (_: Exception){

        }
    }

    fun deleteChild(childId: Long){
        val c = repository.findById(childId)
        if (c.isPresent){
            repository.delete(c.get())
        }
    }

    fun deleteAllParentChildren(parentId: Long){
        val parentChildren = repository.getAllChildByParentId(parentId)
        if (parentChildren.isNotEmpty()){
            repository.deleteAll(parentChildren)
        }
    }
}