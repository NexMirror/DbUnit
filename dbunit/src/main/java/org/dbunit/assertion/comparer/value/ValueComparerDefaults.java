package org.dbunit.assertion.comparer.value;

import java.util.Map;

/**
 * Default {@link ValueComparer}s, used when one is not specified by a test.
 *
 * @author Jeff Jensen
 * @since 2.6.0
 */
public interface ValueComparerDefaults
{
    ValueComparer getDefaultValueComparer();

    Map<String, Map<String, ValueComparer>> getDefaultTableColumnValueComparerMap();

    Map<String, ValueComparer> getDefaultColumnValueComparerMapForTable(
            String tableName);
}
