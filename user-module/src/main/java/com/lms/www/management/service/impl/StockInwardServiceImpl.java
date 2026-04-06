package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.model.InventoryTransaction;
import com.lms.www.management.model.Item;
import com.lms.www.management.model.StockInward;
import com.lms.www.management.model.Vendor;
import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.repository.InventoryTransactionRepository;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.repository.StockInwardRepository;
import com.lms.www.management.repository.VendorRepository;
import com.lms.www.management.service.StockInwardService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInwardServiceImpl implements StockInwardService {

    private final StockInwardRepository stockInwardRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final VendorRepository vendorRepository;
    private final ItemRepository itemRepository;

    // ✅ CREATE (NO FILE)
    @Override
    public StockInward createStockInward(StockInward stockInward) {

        Vendor vendor = vendorRepository.findById(stockInward.getVendor().getId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!"ACTIVE".equals(vendor.getStatus())) {
            throw new RuntimeException("Vendor is inactive");
        }

        Item item = itemRepository.findById(stockInward.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        stockInward.setVendor(vendor);
        stockInward.setItem(item);

        if (stockInward.getPoReference() == null) {
            stockInward.setPoReference("PO-" + System.currentTimeMillis());
        }

        if (stockInward.getStatus() == null) {
            stockInward.setStatus("RECEIVED");
        }

        stockInward.setInvoiceFile("N/A");

        stockInward.setCreatedAt(LocalDateTime.now());
        stockInward.setReceivedDate(LocalDate.now());

        int qty = stockInward.getQuantityReceived() != null ? stockInward.getQuantityReceived() : 0;

        if (qty <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        double cost = stockInward.getCostPerUnit() != null ? stockInward.getCostPerUnit() : 0;

        double total = qty * cost;

        if (stockInward.getTaxPercent() != null) {
            total += total * stockInward.getTaxPercent() / 100;
        }

        stockInward.setTotalAmount(total);

        // 1️⃣ SAVE
        StockInward saved = stockInwardRepository.save(stockInward);

        // 2️⃣ UPDATE STOCK
        InventoryStock inventory = inventoryStockRepository.findByItem(item)
                .orElseGet(() -> InventoryStock.builder()
                        .item(item)
                        .availableStock(0)
                        .reservedStock(0)
                        .damagedStock(0)
                        .totalStock(0)
                        .createdAt(LocalDateTime.now())
                        .build());

        int beforeStock = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
        int available = beforeStock + qty;

        inventory.setAvailableStock(available);
        inventory.setTotalStock(
                (inventory.getReservedStock() != null ? inventory.getReservedStock() : 0)
                        + (inventory.getDamagedStock() != null ? inventory.getDamagedStock() : 0)
                        + available);

        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryStockRepository.save(inventory);

        // 3️⃣ TRANSACTION
        InventoryTransaction txn = InventoryTransaction.builder()
                .item(item)
                .transactionType("INWARD")
                .quantity(qty)
                .beforeStock(beforeStock)
                .afterStock(available)
                .referenceType("STOCK_INWARD")
                .referenceId(saved.getId())
                .performedBy(stockInward.getReceivedBy())
                .createdAt(LocalDateTime.now())
                .build();

        inventoryTransactionRepository.save(txn);

        return saved;
    }

    // ✅ UPLOAD FILE (SEPARATE API)
    @Override
    public StockInward uploadInvoice(Long id, MultipartFile file) {

        StockInward stockInward = stockInwardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock inward not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String uploadDir = "uploads/";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String fileName = System.currentTimeMillis() + "" + originalName.replaceAll("\\s+", "");

        Path filePath = Paths.get(uploadDir + fileName);

        try {
            Files.write(filePath, file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }

        stockInward.setInvoiceFile(uploadDir + fileName);

        return stockInwardRepository.save(stockInward);
    }

    @Override
    public List<StockInward> getAllStockInward() {
        return stockInwardRepository.findAll();
    }

    @Override
    public StockInward getStockInwardById(Long id) {
        return stockInwardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock inward not found"));
    }
}