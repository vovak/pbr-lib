package astminer.filters

import astminer.common.createBamboo
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FileFiltersTest {
    @Test
    fun `test TreeSizeFilter for 100 should exclude bamboo of length 101`() {
        val node = createBamboo(101).toParseResult()
        assertFalse { TreeSizeFilter(maxSize = 100).validate(node) }
    }

    @Test
    fun `test TreeSizeFilter for 10 should not exclude bamboo of length 5`() {
        val node = createBamboo(5).toParseResult()
        assertTrue { TreeSizeFilter(maxSize = 10).validate(node) }
    }

    @Test
    fun `test TreeSizeFilter for minSize 10 should exclude bamboo of size 5`() {
        val node = createBamboo(5).toParseResult()
        assertFalse { TreeSizeFilter(minSize = 10).validate(node) }
    }

    @Test
    fun `test TreeSizeFilter for minSize 10 should not exclude bamboo of size 100`() {
        val node = createBamboo(100).toParseResult()
        assertTrue { TreeSizeFilter(minSize = 10).validate(node) }
    }
}
