package com.hapley.pocketqr.di

import com.hapley.pocketqr.features.barcode.data.BarcodeEntity
import com.hapley.pocketqr.features.barcode.data.BarcodeRepository
import com.hapley.pocketqr.features.barcode.domain.*
import khronos.Dates
import khronos.day
import khronos.days
import khronos.hours
import khronos.month
import khronos.months
import khronos.week
import khronos.weeks
import khronos.year
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MockDataGenerator @Inject constructor(private val barcodeRepository: BarcodeRepository) {

    fun inject() {
        GlobalScope.launch {
            generateData().forEach { barcodeRepository.insert(it) }
        }
    }

    private fun generateData(): List<BarcodeEntity> {
        return mutableListOf<BarcodeEntity>().apply {
            add(BarcodeEntity("https://www.safeentry-qr.gov.sg", "Safe Entry QR", "https://www.safeentry-qr.gov.sg", Dates.today.time, 256, URL, true, 0))
            add(BarcodeEntity("9555540000085", "ISBN", "9555540000085", Dates.yesterday.time, 32, ISBN, false, 0))
            add(BarcodeEntity("WIFI:S:Wifi Name;T:WPA;P:galaxies;;", "Wifi", "9555540000085", 1.week.ago.time, 256, WIFI, false, 0))
            add(BarcodeEntity("mailto:dev@hapley.org?subject=email subject&body=message", "dev@hapley.org", "dev@hapley.org", 1.month.ago.time, 256, EMAIL, false, 10))
            add(BarcodeEntity("https://www.google.com", "google", "google", 12.hours.ago.time, 256, URL, true, 0))
            add(BarcodeEntity("tel:1234", "", "1234", 1.year.ago.time, 256, PHONE, false, 3))
            add(
                BarcodeEntity(
                    "https://maps.google.com/local?q=1.2864726270678333,103.82698018585205", "Location", "https://maps.google.com/local?q=1.2864726270678333,103.82698018585205",
                    30.days.ago.time, 256, URL, false, 0
                )
            )
            add(
                BarcodeEntity(
                    "BEGIN:VCARD\n" +
                        "VERSION:3.0\n" +
                        "N:Doe;John\n" +
                        "FN:John Doe\n" +
                        "TEL;TYPE=voice,work,pref:98123456\n" +
                        "END:VCARD",
                    "", "John Doe", 3.months.ago.time, 256, CONTACT, false, 0
                )
            )
            add(BarcodeEntity("12345678", "Code 39", "12345678", 3.hours.ago.time, CODE_39, UNKNOWN, false, 0))
            add(BarcodeEntity("12812345", "Code 128", "12345678", 6.hours.ago.time, CODE_128, UNKNOWN, false, 0))
            add(BarcodeEntity("09312345", "Code 93", "12345678", 10.hours.ago.time, CODE_93, UNKNOWN, false, 0))
            add(BarcodeEntity("12345678901234", "Codebar", "12345678", 10.hours.ago.time, CODEBAR, UNKNOWN, false, 0))
            add(BarcodeEntity("042100005264", "UPC-A", "042100005264", 3.day.ago.time, UPC_A, UNKNOWN, false, 0))
            add(BarcodeEntity("12345678", "UPC-E", "12345678", 1.day.ago.time, UPC_E, UNKNOWN, false, 0))
        }
    }
}
