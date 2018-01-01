package org.dbunit.assertion.comparer.value;

import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * Strategy for selecting a {@link ValueComparer} from a {@link Map} of them.
 *
 * @author Jeff Jensen
 *
 * @since 2.6.0
 */
@FunctionalInterface
public interface ValueComparerSelector
{
    /**
     * @return The selected {@link ValueComparer} from the specified
     *         valueComparers map.
     * @throws DatabaseUnitException
     */
    ValueComparer select(ITable expectedTable, ITable actualTable, int rowNum,
            String columnName, DataType dataType, Object expectedValue,
            Object actualValue, Map<Object, ValueComparer> valueComparers)
            throws DatabaseUnitException;
}
