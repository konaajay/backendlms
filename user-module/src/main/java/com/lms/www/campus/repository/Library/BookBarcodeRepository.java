package com.lms.www.campus.repository.Library;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Library.BookBarcode;

@Repository
public interface BookBarcodeRepository extends JpaRepository<BookBarcode, Long> {
    Optional<BookBarcode> findByBarcodeValue(String barcodeValue);
}
