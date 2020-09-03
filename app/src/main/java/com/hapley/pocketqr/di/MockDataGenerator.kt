package com.hapley.pocketqr.di

import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository
import com.hapley.pocketqr.features.barcode.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MockDataGenerator(private val barcodeRepository: BarcodeRepository) {

    fun inject() {
        GlobalScope.launch {
            generateData().forEach { barcodeRepository.insert(it) }
        }
    }

    private fun generateData(): List<BarcodeEntity> {
        return mutableListOf<BarcodeEntity>().apply {
            add(BarcodeEntity("https://www.safeentry-qr.gov.sg/tenant/PROD-200105648H-TIONGBAHRUPLAZA-TP", "TB Plaza", "TB Plaza", Date().time, 256, URL, true, 0))
            add(BarcodeEntity("9555540000085", "", "9555540000085", 1598265090208, 32, ISBN, false, 0))
            add(BarcodeEntity("SMSTO:+12345:Hello world", "", "+12345\nHello world", 1598450490804, 256, SMS, false, 0))
            add(BarcodeEntity("WIFI:S:We know;T:WPA;P:galaxies;;", "", "9555540000085", 1598450497532, 256, WIFI, false, 0))
            add(BarcodeEntity("mailto:dev@hapley.org?subject=email subject&body=message", "", "dev@hapley.org", 1598450505802, 256, EMAIL, false, 10))
            add(BarcodeEntity("SMSTO:+65 9866 1324:Hello world", "", "+65 9866 1324 Hello world", 1598450510111, 256, SMS, false, 0))
            add(BarcodeEntity("https://www.google.com", "", "https://www.google.com", 1598450518972, 256, URL, true, 0))
            add(BarcodeEntity("tel:1234", "", "1234", 1598450521917, 256, PHONE, false, 3))
            add(
                BarcodeEntity(
                    "https://maps.google.com/local?q=1.2864726270678333,103.82698018585205", "", "https://maps.google.com/local?q=1.2864726270678333,103.82698018585205",
                    1598450500097, 256, 8, false, 0
                )
            )
            add(
                BarcodeEntity(
                    "BEGIN:VCARD\n" +
                            "VERSION:3.0\n" +
                            "N:halim;Fernando\n" +
                            "FN:Fernando halim\n" +
                            "TEL;TYPE=voice,work,pref:98661324\n" +
                            "END:VCARD", "", "Fernando halim", 1598450493112, 256, CONTACT, false, 0
                )
            )
        }
    }
}