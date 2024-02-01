package com.hulkdx.findprofessional.common.feature.book

import com.hulkdx.findprofessional.common.feature.book.BookUiState.BookingTime
import com.hulkdx.findprofessional.common.feature.book.BookUiState.BookingTime.Type.Available
import com.hulkdx.findprofessional.common.feature.book.BookUiState.BookingTime.Type.Selected
import com.hulkdx.findprofessional.common.feature.book.BookUiState.BookingTime.Type.UnAvailable
import com.hulkdx.findprofessional.common.feature.home.model.ProfessionalAvailability
import com.hulkdx.findprofessional.common.utils.createBookingTimes
import com.hulkdx.findprofessional.common.utils.createProfessional
import com.hulkdx.findprofessional.common.utils.now
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BookUseCaseTest {

    private lateinit var sut: BookUseCase

    @BeforeTest
    fun setUp() {
        sut = createSut()
    }

    @Test
    fun `isAvailabilityIncludedInTimes tests`() {
        data class TestData(
            val availabilityFrom: Int,
            val availabilityTo: Int,
            val from: Int,
            val to: Int,
            val exceptedResult: Boolean,
        )

        val testData = listOf(
            TestData(
                availabilityFrom = 0,
                availabilityTo = 30,
                from = 0,
                to = 30,
                exceptedResult = true,
            ),
            TestData(
                availabilityFrom = 0,
                availabilityTo = 0,
                from = 0,
                to = 30,
                exceptedResult = true,
            ),
            TestData(
                availabilityFrom = 0,
                availabilityTo = 30,
                from = 30,
                to = 60,
                exceptedResult = false,
            ),
            TestData(
                availabilityFrom = 0,
                availabilityTo = 90,
                from = 60,
                to = 90,
                exceptedResult = true,
            ),
            TestData(
                availabilityFrom = 23 * 60 + 30,
                availabilityTo = 0,
                from = 23 * 60 + 30,
                to = 24 * 60,
                exceptedResult = true,
            ),
        )

        for (t in testData) {
            // Arrange
            val availability = ProfessionalAvailability(
                date = LocalDate.now(), // irrelevant
                from = LocalTime.fromSecondOfDay(t.availabilityFrom * 60),
                to = LocalTime.fromSecondOfDay(t.availabilityTo * 60),
            )
            // Act
            val result = sut.isAvailabilityIncludedInTimes(availability, t.from, t.to)
            // Assert
            assertEquals(t.exceptedResult, result)
        }
    }

    @Test
    fun `getTimes when empty availability then result would be all unavailable times`() {
        // Arrange
        val date = LocalDate.now()
        val professional = createProfessional(availability = listOf())
        val expectedResult = allUnavailable()
        // Act
        val result = sut.getTimes(professional, date, setOf())
        // Assert
        assertEquals(result, expectedResult)
    }

    @Test
    fun `getTimes when all times are available then result would be available`() {
        // Arrange
        val date = LocalDate.now()
        val professional = createProfessionalWithAvailability(date, 0 to 0)
        val expectedResult = allAvailable()
        // Act
        val result = sut.getTimes(professional, date, setOf())
        // Assert
        assertEquals(result, expectedResult)
    }

    @Test
    fun `getUiState when availability is for all way but wrong date should be all unavailable`() =
        runTest {
            // Arrange
            val now = LocalDate(2024, 1, 1)
            val sut = createSut(now)
            val pro = createProfessional(
                availability = listOf(
                    ProfessionalAvailability(
                        date = LocalDate(2024, 1, 2),
                        from = LocalTime.fromSecondOfDay(0),
                        to = LocalTime.fromSecondOfDay(0),
                    )
                )
            )
            // Act
            val result = sut.getUiState(pro).first().times
            // Assert
            assertEquals(result, allUnavailable())
        }

    @Test
    fun `currentDay tests`() {
        // Arrange
        val now = LocalDate(2024, 1, 1)
        // Act
        val result = sut.currentDay(now)
        // Assert
        assertEquals("1.1.2024", result)
    }

    @Test
    fun `dayMinusOne tests`() = runTest {
        // Arrange
        val now = LocalDate(2024, 1, 2)
        val pro = createProfessional()
        val sut = createSut(now)
        // Act
        sut.dayMinusOne()
        val result = sut.getUiState(pro).first().currentDate
        // Assert
        assertEquals("1.1.2024", result)
    }

    @Test
    fun `dayPlusOne tests`() = runTest {
        // Arrange
        val now = LocalDate(2024, 1, 2)
        val pro = createProfessional()
        val sut = createSut(now)
        // Act
        sut.dayPlusOne()
        val result = sut.getUiState(pro).first().currentDate
        // Assert
        assertEquals("3.1.2024", result)
    }

    @Test
    fun `onTimeClicked on available item then the type should be Selected`() = runTest {
        // Arrange
        val selectedTimeId = 30
        val date = LocalDate.now()
        val professional = createProfessionalWithAvailability(date, 0 to 0)
        // Act
        sut.onTimeClicked(createBookingTimes(selectedTimeId))
        val result = sut.getUiState(professional).first().times
        // Assert
        val actual = result
            .flatten()
            .find { it.id == selectedTimeId }
            ?.type
        assertEquals(Selected, actual)
    }

    @Test
    fun `double onTimeClicked on available item then the type should be Available`() = runTest {
        // Arrange
        val selectedTimeId = 30
        val date = LocalDate.now()
        val professional = createProfessionalWithAvailability(date, 0 to 0)
        // Act
        sut.onTimeClicked(createBookingTimes(selectedTimeId))
        sut.onTimeClicked(createBookingTimes(selectedTimeId))
        val result = sut.getUiState(professional).first().times
        // Assert
        val actual = result
            .flatten()
            .find { it.id == selectedTimeId }
            ?.type
        assertEquals(Available, actual)
    }

    private fun createSut(now: LocalDate = LocalDate.now()) = BookUseCase(now)

    private fun createProfessionalWithAvailability(date: LocalDate, vararg times: Pair<Int, Int>) =
        createProfessional(
            availability = times.map {
                ProfessionalAvailability(
                    date = date,
                    from = LocalTime.fromSecondOfDay(it.first * 60),
                    to = LocalTime.fromSecondOfDay(it.second * 60),
                )
            }
        )

    private fun allUnavailable() = listOf(
        BookingTime(
            id = 0,
            startTime = "00:00",
            endTime = "00:30",
            type = UnAvailable,
        ), BookingTime(
            id = 30,
            startTime = "00:30",
            endTime = "01:00",
            type = UnAvailable,
        ), BookingTime(
            id = 60,
            startTime = "01:00",
            endTime = "01:30",
            type = UnAvailable,
        ), BookingTime(
            id = 90,
            startTime = "01:30",
            endTime = "02:00",
            type = UnAvailable,
        ), BookingTime(
            id = 120,
            startTime = "02:00",
            endTime = "02:30",
            type = UnAvailable,
        ), BookingTime(
            id = 150,
            startTime = "02:30",
            endTime = "03:00",
            type = UnAvailable,
        ), BookingTime(
            id = 180,
            startTime = "03:00",
            endTime = "03:30",
            type = UnAvailable,
        ), BookingTime(
            id = 210,
            startTime = "03:30",
            endTime = "04:00",
            type = UnAvailable,
        ), BookingTime(
            id = 240,
            startTime = "04:00",
            endTime = "04:30",
            type = UnAvailable,
        ), BookingTime(
            id = 270,
            startTime = "04:30",
            endTime = "05:00",
            type = UnAvailable,
        ), BookingTime(
            id = 300,
            startTime = "05:00",
            endTime = "05:30",
            type = UnAvailable,
        ), BookingTime(
            id = 330,
            startTime = "05:30",
            endTime = "06:00",
            type = UnAvailable,
        ), BookingTime(
            id = 360,
            startTime = "06:00",
            endTime = "06:30",
            type = UnAvailable,
        ), BookingTime(
            id = 390,
            startTime = "06:30",
            endTime = "07:00",
            type = UnAvailable,
        ), BookingTime(
            id = 420,
            startTime = "07:00",
            endTime = "07:30",
            type = UnAvailable,
        ), BookingTime(
            id = 450,
            startTime = "07:30",
            endTime = "08:00",
            type = UnAvailable,
        ), BookingTime(
            id = 480,
            startTime = "08:00",
            endTime = "08:30",
            type = UnAvailable,
        ), BookingTime(
            id = 510,
            startTime = "08:30",
            endTime = "09:00",
            type = UnAvailable,
        ), BookingTime(
            id = 540,
            startTime = "09:00",
            endTime = "09:30",
            type = UnAvailable,
        ), BookingTime(
            id = 570,
            startTime = "09:30",
            endTime = "10:00",
            type = UnAvailable,
        ), BookingTime(
            id = 600,
            startTime = "10:00",
            endTime = "10:30",
            type = UnAvailable,
        ), BookingTime(
            id = 630,
            startTime = "10:30",
            endTime = "11:00",
            type = UnAvailable,
        ), BookingTime(
            id = 660,
            startTime = "11:00",
            endTime = "11:30",
            type = UnAvailable,
        ), BookingTime(
            id = 690,
            startTime = "11:30",
            endTime = "12:00",
            type = UnAvailable,
        ), BookingTime(
            id = 720,
            startTime = "12:00",
            endTime = "12:30",
            type = UnAvailable,
        ), BookingTime(
            id = 750,
            startTime = "12:30",
            endTime = "13:00",
            type = UnAvailable,
        ), BookingTime(
            id = 780,
            startTime = "13:00",
            endTime = "13:30",
            type = UnAvailable,
        ), BookingTime(
            id = 810,
            startTime = "13:30",
            endTime = "14:00",
            type = UnAvailable,
        ), BookingTime(
            id = 840,
            startTime = "14:00",
            endTime = "14:30",
            type = UnAvailable,
        ), BookingTime(
            id = 870,
            startTime = "14:30",
            endTime = "15:00",
            type = UnAvailable,
        ), BookingTime(
            id = 900,
            startTime = "15:00",
            endTime = "15:30",
            type = UnAvailable,
        ), BookingTime(
            id = 930,
            startTime = "15:30",
            endTime = "16:00",
            type = UnAvailable,
        ), BookingTime(
            id = 960,
            startTime = "16:00",
            endTime = "16:30",
            type = UnAvailable,
        ), BookingTime(
            id = 990,
            startTime = "16:30",
            endTime = "17:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1020,
            startTime = "17:00",
            endTime = "17:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1050,
            startTime = "17:30",
            endTime = "18:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1080,
            startTime = "18:00",
            endTime = "18:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1110,
            startTime = "18:30",
            endTime = "19:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1140,
            startTime = "19:00",
            endTime = "19:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1170,
            startTime = "19:30",
            endTime = "20:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1200,
            startTime = "20:00",
            endTime = "20:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1230,
            startTime = "20:30",
            endTime = "21:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1260,
            startTime = "21:00",
            endTime = "21:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1290,
            startTime = "21:30",
            endTime = "22:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1320,
            startTime = "22:00",
            endTime = "22:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1350,
            startTime = "22:30",
            endTime = "23:00",
            type = UnAvailable,
        ), BookingTime(
            id = 1380,
            startTime = "23:00",
            endTime = "23:30",
            type = UnAvailable,
        ), BookingTime(
            id = 1410,
            startTime = "23:30",
            endTime = "00:00",
            type = UnAvailable,
        )
    )
        .chunked(2)

    private fun allAvailable() = listOf(
        BookingTime(
            id = 0,
            startTime = "00:00",
            endTime = "00:30",
            type = Available,
        ), BookingTime(
            id = 30,
            startTime = "00:30",
            endTime = "01:00",
            type = Available,
        ), BookingTime(
            id = 60,
            startTime = "01:00",
            endTime = "01:30",
            type = Available,
        ), BookingTime(
            id = 90,
            startTime = "01:30",
            endTime = "02:00",
            type = Available,
        ), BookingTime(
            id = 120,
            startTime = "02:00",
            endTime = "02:30",
            type = Available,
        ), BookingTime(
            id = 150,
            startTime = "02:30",
            endTime = "03:00",
            type = Available,
        ), BookingTime(
            id = 180,
            startTime = "03:00",
            endTime = "03:30",
            type = Available,
        ), BookingTime(
            id = 210,
            startTime = "03:30",
            endTime = "04:00",
            type = Available,
        ), BookingTime(
            id = 240,
            startTime = "04:00",
            endTime = "04:30",
            type = Available,
        ), BookingTime(
            id = 270,
            startTime = "04:30",
            endTime = "05:00",
            type = Available,
        ), BookingTime(
            id = 300,
            startTime = "05:00",
            endTime = "05:30",
            type = Available,
        ), BookingTime(
            id = 330,
            startTime = "05:30",
            endTime = "06:00",
            type = Available,
        ), BookingTime(
            id = 360,
            startTime = "06:00",
            endTime = "06:30",
            type = Available,
        ), BookingTime(
            id = 390,
            startTime = "06:30",
            endTime = "07:00",
            type = Available,
        ), BookingTime(
            id = 420,
            startTime = "07:00",
            endTime = "07:30",
            type = Available,
        ), BookingTime(
            id = 450,
            startTime = "07:30",
            endTime = "08:00",
            type = Available,
        ), BookingTime(
            id = 480,
            startTime = "08:00",
            endTime = "08:30",
            type = Available,
        ), BookingTime(
            id = 510,
            startTime = "08:30",
            endTime = "09:00",
            type = Available,
        ), BookingTime(
            id = 540,
            startTime = "09:00",
            endTime = "09:30",
            type = Available,
        ), BookingTime(
            id = 570,
            startTime = "09:30",
            endTime = "10:00",
            type = Available,
        ), BookingTime(
            id = 600,
            startTime = "10:00",
            endTime = "10:30",
            type = Available,
        ), BookingTime(
            id = 630,
            startTime = "10:30",
            endTime = "11:00",
            type = Available,
        ), BookingTime(
            id = 660,
            startTime = "11:00",
            endTime = "11:30",
            type = Available,
        ), BookingTime(
            id = 690,
            startTime = "11:30",
            endTime = "12:00",
            type = Available,
        ), BookingTime(
            id = 720,
            startTime = "12:00",
            endTime = "12:30",
            type = Available,
        ), BookingTime(
            id = 750,
            startTime = "12:30",
            endTime = "13:00",
            type = Available,
        ), BookingTime(
            id = 780,
            startTime = "13:00",
            endTime = "13:30",
            type = Available,
        ), BookingTime(
            id = 810,
            startTime = "13:30",
            endTime = "14:00",
            type = Available,
        ), BookingTime(
            id = 840,
            startTime = "14:00",
            endTime = "14:30",
            type = Available,
        ), BookingTime(
            id = 870,
            startTime = "14:30",
            endTime = "15:00",
            type = Available,
        ), BookingTime(
            id = 900,
            startTime = "15:00",
            endTime = "15:30",
            type = Available,
        ), BookingTime(
            id = 930,
            startTime = "15:30",
            endTime = "16:00",
            type = Available,
        ), BookingTime(
            id = 960,
            startTime = "16:00",
            endTime = "16:30",
            type = Available,
        ), BookingTime(
            id = 990,
            startTime = "16:30",
            endTime = "17:00",
            type = Available,
        ), BookingTime(
            id = 1020,
            startTime = "17:00",
            endTime = "17:30",
            type = Available,
        ), BookingTime(
            id = 1050,
            startTime = "17:30",
            endTime = "18:00",
            type = Available,
        ), BookingTime(
            id = 1080,
            startTime = "18:00",
            endTime = "18:30",
            type = Available,
        ), BookingTime(
            id = 1110,
            startTime = "18:30",
            endTime = "19:00",
            type = Available,
        ), BookingTime(
            id = 1140,
            startTime = "19:00",
            endTime = "19:30",
            type = Available,
        ), BookingTime(
            id = 1170,
            startTime = "19:30",
            endTime = "20:00",
            type = Available,
        ), BookingTime(
            id = 1200,
            startTime = "20:00",
            endTime = "20:30",
            type = Available,
        ), BookingTime(
            id = 1230,
            startTime = "20:30",
            endTime = "21:00",
            type = Available,
        ), BookingTime(
            id = 1260,
            startTime = "21:00",
            endTime = "21:30",
            type = Available,
        ), BookingTime(
            id = 1290,
            startTime = "21:30",
            endTime = "22:00",
            type = Available,
        ), BookingTime(
            id = 1320,
            startTime = "22:00",
            endTime = "22:30",
            type = Available,
        ), BookingTime(
            id = 1350,
            startTime = "22:30",
            endTime = "23:00",
            type = Available,
        ), BookingTime(
            id = 1380,
            startTime = "23:00",
            endTime = "23:30",
            type = Available,
        ), BookingTime(
            id = 1410,
            startTime = "23:30",
            endTime = "00:00",
            type = Available,
        )
    )
        .chunked(2)
}
