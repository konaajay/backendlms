/*
 * package com.lms.www.controller; import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.web.bind.annotation.DeleteMapping; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PatchMapping; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.PutMapping; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RestController; import
 * com.lms.www.model.Documents.Document; import
 * com.lms.www.model.Documents.DocumentAccessLog; import
 * com.lms.www.model.Documents.DocumentCategory; import
 * com.lms.www.model.Documents.DocumentShare; import
 * com.lms.www.model.Documents.DocumentVersion; import
 * com.lms.www.service.Imp.DocumentServiceImpl;
 * 
 * import jakarta.servlet.http.HttpServletRequest; import
 * lombok.RequiredArgsConstructor;
 * 
 * @RestController
 * 
 * @RequestMapping("/documents")
 * 
 * @RequiredArgsConstructor public class DocumentController {
 * 
 * @Autowired private DocumentServiceImpl service;
 * 
 * 
 * 
 * 
 * //========================== DOCUMENT ========================== //
 * 
 * @PostMapping public Document uploadDocument(@RequestBody Document document,
 * HttpServletRequest request) { Long userId = extractUserIdFromToken(request);
 * document.setUploadedBy(userId); document.setOwnerUserId(userId); return
 * service.uploadDocument(document); }
 * 
 * private Long extractUserIdFromToken(HttpServletRequest request) { return
 * null; }
 * 
 * @GetMapping public List<Document> getAllDocuments() { return
 * service.getAllDocuments(); }
 * 
 * @GetMapping("/{id}") public Document getDocumentById(@PathVariable Long id) {
 * return service.getDocumentById(id); }
 * 
 * @PutMapping("/{id}") public Document updateDocument(@PathVariable Long
 * id, @RequestBody Document document, HttpServletRequest request) { Long userId
 * = extractUserIdFromToken(request); return service.updateDocument(id,
 * document, userId); }
 * 
 * @PatchMapping("/{id}") public Document patchDocument(@PathVariable Long
 * id, @RequestBody Document document, HttpServletRequest request) { Long userId
 * = extractUserIdFromToken(request); return service.patchDocument(id, document,
 * userId); }
 * 
 * @DeleteMapping("/{id}") public String deleteDocument(@PathVariable Long id,
 * HttpServletRequest request) { Long userId = extractUserIdFromToken(request);
 * service.deleteDocument(id, userId); return "Document deleted successfully"; }
 * 
 * //========================= DOCUMENT CATEGORY ========================== //
 * 
 * 
 * @PostMapping("/categories") public DocumentCategory
 * createCategory(@RequestBody DocumentCategory category) { return
 * service.createCategory(category); }
 * 
 * @GetMapping("/categories") public List<DocumentCategory> getAllCategories() {
 * return service.getAllCategories(); }
 * 
 * @GetMapping("/categories/{id}") public DocumentCategory
 * getCategoryById(@PathVariable Long id) { return service.getCategoryById(id);
 * }
 * 
 * @PutMapping("/categories/{id}") public DocumentCategory
 * updateCategory(@PathVariable Long id, @RequestBody DocumentCategory category)
 * { return service.updateCategory(id, category); }
 * 
 * @PatchMapping("/categories/{id}") public DocumentCategory
 * patchCategory(@PathVariable Long id, @RequestBody DocumentCategory category)
 * { return service.patchCategory(id, category); }
 * 
 * @DeleteMapping("/categories/{id}") public String deleteCategory(@PathVariable
 * Long id) { service.deleteCategory(id); return
 * "Category deleted successfully"; }
 * 
 * //==========================DOCUMENT ACCESS LOG========================== //
 * 
 * @PostMapping("/logs") public DocumentAccessLog logDocumentAction(@RequestBody
 * DocumentAccessLog log, HttpServletRequest request) { Long userId =
 * extractUserIdFromToken(request); log.setUserId(userId); return
 * service.logAction(log); }
 * 
 * @GetMapping("/logs") public List<DocumentAccessLog> getAllLogs() { return
 * service.getAllLogs(); }
 * 
 * @GetMapping("/logs/{id}") public DocumentAccessLog getLogById(@PathVariable
 * Long id) { return service.getLogById(id); }
 * 
 * @PutMapping("/logs/{id}") public DocumentAccessLog updateLog(@PathVariable
 * Long id, @RequestBody DocumentAccessLog log) { return service.updateLog(id,
 * log); }
 * 
 * @PatchMapping("/logs/{id}") public DocumentAccessLog patchLog(@PathVariable
 * Long id, @RequestBody DocumentAccessLog log) { return service.patchLog(id,
 * log); }
 * 
 * @DeleteMapping("/logs/{id}") public String deleteLog(@PathVariable Long id) {
 * service.deleteLog(id); return "Log deleted successfully"; }
 * 
 * // ========================DOCUMENT VERSION ========================== //
 * 
 * @PostMapping("/versions") public DocumentVersion
 * addDocumentVersion(@RequestBody DocumentVersion version) { return
 * service.addDocumentVersion(version); }
 * 
 * @GetMapping("/{documentId}/versions") public List<DocumentVersion>
 * getVersions(@PathVariable Long documentId) { return
 * service.getVersionsByDocument(documentId); }
 * 
 * @GetMapping("/versions/{id}") public DocumentVersion
 * getVersionById(@PathVariable Long id) { return service.getVersionById(id); }
 * 
 * @PutMapping("/versions/{id}") public DocumentVersion
 * updateVersion(@PathVariable Long id, @RequestBody DocumentVersion version) {
 * return service.updateVersion(id, version); }
 * 
 * @PatchMapping("/versions/{id}") public DocumentVersion
 * patchVersion(@PathVariable Long id, @RequestBody DocumentVersion version) {
 * return service.patchVersion(id, version); }
 * 
 * @DeleteMapping("/versions/{id}") public String deleteVersion(@PathVariable
 * Long id) { service.deleteVersion(id); return "Version deleted successfully";
 * } //========================== DOCUMENT SHARE ========================== //
 * 
 * @PostMapping("/share") public DocumentShare shareDocument(@RequestBody
 * DocumentShare share, HttpServletRequest request) { Long userId =
 * extractUserIdFromToken(request); share.setSharedBy(userId); return
 * service.shareDocument(share); }
 * 
 * @GetMapping("/shared") public List<DocumentShare>
 * getSharedDocuments(HttpServletRequest request) { Long userId =
 * extractUserIdFromToken(request); return
 * service.getDocumentsSharedWithUser(userId); }
 * 
 * @GetMapping("/share/{id}") public DocumentShare
 * getSharedDocumentById(@PathVariable Long id) { return
 * service.getSharedDocumentById(id); }
 * 
 * @PutMapping("/share/{id}") public DocumentShare
 * updateSharedDocument(@PathVariable Long id, @RequestBody DocumentShare share)
 * { return service.updateSharedDocument(id, share); }
 * 
 * @PatchMapping("/share/{id}") public DocumentShare
 * patchSharedDocument(@PathVariable Long id, @RequestBody DocumentShare share)
 * { return service.patchSharedDocument(id, share); }
 * 
 * @DeleteMapping("/share/{id}") public String
 * deleteSharedDocument(@PathVariable Long id) {
 * service.deleteSharedDocument(id); return
 * "Shared document deleted successfully"; } }
 */


