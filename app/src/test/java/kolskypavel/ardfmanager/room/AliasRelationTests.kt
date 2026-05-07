package kolskypavel.ardfmanager.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kolskypavel.ardfmanager.backend.room.database.EventDatabase
import kolskypavel.ardfmanager.backend.room.entity.Alias
import kolskypavel.ardfmanager.backend.room.entity.Punch
import kolskypavel.ardfmanager.backend.room.entity.Race
import kolskypavel.ardfmanager.backend.room.entity.Result
import kolskypavel.ardfmanager.backend.room.enums.PunchStatus
import kolskypavel.ardfmanager.backend.room.enums.SIRecordType
import kolskypavel.ardfmanager.backend.sportident.SITime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class AliasRelationTests {
    private var database: EventDatabase? = null

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, EventDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database?.close()
    }

    @Test
    fun resultDataUsesAliasFromResultRace() = runTest {
        val database = database!!
        val raceA = Race()
        val raceB = Race()
        val resultA = createResult(raceA.id)
        val resultB = createResult(raceB.id)

        database.raceDao().createRace(raceA)
        database.raceDao().createRace(raceB)
        database.aliasDao().createOrUpdateAlias(Alias(UUID.randomUUID(), raceA.id, 31, "Fox 1"))
        database.aliasDao().createOrUpdateAlias(Alias(UUID.randomUUID(), raceB.id, 31, "Fox A"))
        database.resultDao().createOrUpdateResult(resultA)
        database.resultDao().createOrUpdateResult(resultB)
        database.punchDao().createOrUpdatePunch(createPunch(raceA.id, resultA.id, 31))
        database.punchDao().createOrUpdatePunch(createPunch(raceB.id, resultB.id, 31))

        val raceAAlias = database.resultDao().getResultData(resultA.id).punches.single().alias?.name
        val raceBAlias = database.resultDao().getResultData(resultB.id).punches.single().alias?.name

        assertEquals("Fox 1", raceAAlias)
        assertEquals("Fox A", raceBAlias)
    }

    private fun createResult(raceId: UUID): Result {
        return Result().also { result ->
            result.raceId = raceId
            result.siNumber = 1000
        }
    }

    private fun createPunch(raceId: UUID, resultId: UUID, siCode: Int): Punch {
        return Punch(
            id = UUID.randomUUID(),
            raceId = raceId,
            resultId = resultId,
            cardNumber = 1000,
            siCode = siCode,
            siTime = SITime(),
            origSiTime = SITime(),
            punchType = SIRecordType.CONTROL,
            order = 0,
            punchStatus = PunchStatus.UNKNOWN,
            split = Duration.ZERO
        )
    }
}
