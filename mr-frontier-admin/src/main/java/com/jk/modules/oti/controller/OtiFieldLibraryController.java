package com.jk.modules.oti.controller;

import com.github.pagehelper.PageInfo;
import com.jk.common.annotation.OperationLog;
import com.jk.common.base.controller.BaseController;
import com.jk.common.security.token.FormToken;
import com.jk.common.security.xss.XssHttpServletRequestWrapper;
import com.jk.modules.oti.model.OtiConfig;
import com.jk.modules.oti.model.OtiFieldLibrary;
import com.jk.modules.oti.service.OtiFieldLibraryService;
import com.jk.modules.sys.model.Permission;
import com.jk.modules.sys.service.PermissionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * oti字段详细配置Controller
 * Created by fengj on 2017/4/30.
 */
@Controller
@RequestMapping("/admin/oti/field")
public class OtiFieldLibraryController extends BaseController{

    private static final String BASE_PATH = "admin/oti/";

    @Resource
    private OtiFieldLibraryService otiFieldLibraryService;

    /**
     * 分页查询字段详细配置列表
     *
     * @param pageNum  当前页码
     * @param msgId    传输消息ID
     * @param modelMap
     * @return
     */
    @RequiresPermissions("oti-field:list")
    @GetMapping
    public String toString(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            String msgId, ModelMap modelMap) throws Exception {
        try {
            log.debug("分页查询解析配置列表参数! pageNum = {}, msgId = {}", pageNum, msgId);
            PageInfo<OtiFieldLibrary> pageInfo = otiFieldLibraryService.findPage(pageNum, 20, msgId);
            log.info("分页查询解析配置列表结果！ pageInfo = {}", pageInfo);
            modelMap.put("pageInfo", pageInfo);
            modelMap.put("msgId", msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BASE_PATH + "otiField-list";
    }

    /**
     * 分页查询字段详细配置列表
     *
     * @param pageNum  当前页码
     * @param msgId    传输消息ID
     * @param modelMap
     * @return
     */
    @RequiresPermissions("oti-field:list")
    @GetMapping(value = "/{msgId}")
    public String list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @PathVariable("msgId") String msgId, ModelMap modelMap) throws Exception {
        try {
            log.debug("分页查询解析配置列表参数! pageNum = {}, msgId = {}", pageNum, msgId);
            PageInfo<OtiFieldLibrary> pageInfo = otiFieldLibraryService.findPage(pageNum, 20, msgId);
            log.info("分页查询解析配置列表结果！ pageInfo = {}", pageInfo);
            modelMap.put("pageInfo", pageInfo);
            modelMap.put("msgId", msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BASE_PATH + "otiField-list";
    }



    /**
     * 根据主键ID删除详情配置
     * @param id
     * @return
     */
    @OperationLog(value = "删除配置")
    @RequiresPermissions("oti-field:delete")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        log.debug("删除配置! id = {}", id);
        if (null == id) {
            log.info("删除配置不存在! id = {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("删除配置不存在!");
        }

        Boolean flag = otiFieldLibraryService.deleteFieldAndArrayOrObject(id);
        if(flag){
            log.info("删除配置成功! id = {}", id);
            return ResponseEntity.ok("删除成功！");
        }

        log.info("删除配置失败，但没有抛出异常! id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 跳转到字段详细配置添加页面
     * @return
     */
    @FormToken(save = true)
    @RequiresPermissions("oti-field:create")
    @GetMapping("/add/{msgId}")
    public String add(@PathVariable("msgId") String msgId, ModelMap modelMap){
        log.info("跳转到配置添加页面!");
        modelMap.put("msgId", msgId);
        return BASE_PATH + msgId +"/otiField-add";
    }

    /**
     * 添加详细配置
     * @param
     * @return
     */
    @FormToken(remove = true)
    @OperationLog(value = "添加权限")
    @RequiresPermissions("otiField-add")
    @ResponseBody
    @PostMapping
    public ModelMap saveOtiFieldLibrary(HttpServletRequest request, OtiFieldLibrary otiFieldLibrary){
        ModelMap messagesMap = new ModelMap();

        log.debug("添加详细配置参数! otiFieldLibrary = {}", otiFieldLibrary);
        if (otiFieldLibraryService.save(otiFieldLibrary) > 0 ) {
            log.info("添加解析配置成功! otiFieldLibraryId = {}", otiFieldLibrary.getId());
            messagesMap.put("status", SUCCESS);
            messagesMap.put("message", "添加成功!");
            return messagesMap;
        }
        log.info("添加解析配置失败, 但没有抛出异常! otiFieldLibraryId = {}", otiFieldLibrary.getId());
        messagesMap.put("status", FAILURE);
        messagesMap.put("message", "添加失败!");
        return messagesMap;
    }

    /**
     * 跳转到详细配置编辑页面
     * @return
     */
    @FormToken(save = true)
    @RequiresPermissions("oti-field:update")
    @GetMapping(value = "/edit/{msgId}/{id}")
    public String edit(@PathVariable("id") Long id, @PathVariable("msgId") String msgId,ModelMap modelMap){
        log.info("跳转到详细配置编辑页面！ msgid = {}, id = {}", msgId, id);
        OtiFieldLibrary otiFieldLibrary = otiFieldLibraryService.findById(id);

        modelMap.put("model", otiFieldLibrary);
        modelMap.put("msgId", msgId);
        return BASE_PATH + "otiField-edit";
    }

    /**
     * 更新详细配置信息
     * @param id
     * @param otiFieldLibrary
     * @return
     */
    @FormToken(remove = true)
    @OperationLog(value = "编辑详细配置")
    @RequiresPermissions("oti-field:update")
    @ResponseBody
    @PutMapping(value = "/{id}")
    public ModelMap updatePermission(HttpServletRequest request,
                                     @PathVariable("id") Long id,
                                     OtiFieldLibrary otiFieldLibrary){
        ModelMap messagesMap = new ModelMap();
        log.debug("编辑详细配置参数! id= {}, otiFieldLibrary = {}", id, otiFieldLibrary);

        if(null == id){
            messagesMap.put("status",FAILURE);
            messagesMap.put("message","ID不能为空!");
            return messagesMap;
        }

        otiFieldLibraryService.updateSelective(otiFieldLibrary);
        log.info("编辑详细配置成功! id= {}, permission = {}", id, otiFieldLibrary);
        messagesMap.put("status",SUCCESS);
        messagesMap.put("message","编辑成功!");
        return messagesMap;
    }

}
