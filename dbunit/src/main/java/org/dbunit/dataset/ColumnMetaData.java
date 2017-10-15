package org.dbunit.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;

/**
 * Wrapper class for column metadata as retrieved by 
 * {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)}.
 * 
 * This is a utility wrapper so that metadata for any number of columns can be cached  
 * in a collection and accessed repeatedly and in any order.  Column metadata are
 * typically returned from the database as a forward-only result set, and cannot be 
 * "rewound" after scrolling forward to a specific column.  
 * 
 * @author Joe Kearns
 * @version $Revision$
 * @since June 2017
 *
 */
public class ColumnMetaData {
	private List<MetaDataField> fields = new ArrayList<MetaDataField>();
	
	private final StringMetaDataField tableCat = new StringMetaDataField("tableCat", 1);
	private final StringMetaDataField tableSchema = new StringMetaDataField("tableSchema", 2);
	private final StringMetaDataField tableName = new StringMetaDataField("tableName", 3);
	private final StringMetaDataField columnName = new StringMetaDataField("columnName", 4);
	private final IntegerMetaDataField sqlType = new IntegerMetaDataField("sqlType", 5);
	private final StringMetaDataField sqlTypeName = new StringMetaDataField("sqlTypeName", 6);
	private final IntegerMetaDataField columnSize = new IntegerMetaDataField("columnSize", 7);
	// 8 not used
	private final IntegerMetaDataField decimalDigits = new IntegerMetaDataField("decimalDigits", 9);
	private final IntegerMetaDataField numPrecRadix = new IntegerMetaDataField("numPrecRadix", 10);
	private final IntegerMetaDataField nullable = new IntegerMetaDataField("nullable", 11);
	private final StringMetaDataField remarks = new StringMetaDataField("remarks", 12);
	private final StringMetaDataField columnDefault = new StringMetaDataField("columnDefault", 13);
	// 14 not used
	// 15 not used
	private final IntegerMetaDataField charOctetLength = new IntegerMetaDataField("charOctetLength", 16);
	private final IntegerMetaDataField ordinalPosition = new IntegerMetaDataField("ordinalPosition", 17);
	private final StringMetaDataField isoNullable = new StringMetaDataField("isoNullable", 18);
	private final StringMetaDataField scopeCatalog = new StringMetaDataField("scopeCatalog", 19);
	private final StringMetaDataField scopeSchema = new StringMetaDataField("scopeSchema", 20);
	private final StringMetaDataField scopeTable = new StringMetaDataField("scopeTable", 21);
	private final ShortMetaDataField sourceDataType = new ShortMetaDataField("sourceDataType", 22);
	private final StringMetaDataField autoincrement = new StringMetaDataField("autoincrement", 23);
	private final StringMetaDataField generatedcolumn = new StringMetaDataField("generatedcolumn", 24);
	
	/**
	 * Construct a ColumnMetaData instance from a result set retrieved by 
	 * {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)}.
	 *
	 * @param resultSet column metadata results from DatabaseMetaData.getColumns
	 * @throws SQLException thrown on from ResultSet accessors
	 */
	public ColumnMetaData(ResultSet resultSet) throws SQLException {
		setDefaults();
		for (MetaDataField field : fields) {
			field.setValue(resultSet);
		}
        //If Types.DISTINCT like SQL DOMAIN, then get Source Date Type of SQL-DOMAIN
        if(sqlType.value == java.sql.Types.DISTINCT)
        {
            sqlType.value = resultSet.getInt("SOURCE_DATA_TYPE");
        }
	}

	/**
	 * Default values for specific fields, consistent with 
	 * {@link org.dbunit.util.SQLHelper#createColumn(ResultSet, org.dbunit.dataset.datatype.IDataTypeFactory, boolean)}
	 */
	private void setDefaults() {
		this.autoincrement.value = Column.AutoIncrement.NO.getKey();
	}
	
	public String getTableCat() {
		return tableCat.getString();
	}
	public String getTableSchema() {
		return tableSchema.getString();
	}
	public String getTableName() {
		return tableName.getString();
	}
	public String getColumnName() {
		return columnName.getString();
	}
	public Integer getSQLType() {
		return sqlType.getInteger();
	}
	public String getSqlTypeName() {
		return sqlTypeName.getString();
	}
	public Integer getColumnSize() {
		return columnSize.getInteger();
	}
	public Integer getDecimalDigits() {
		return decimalDigits.getInteger();
	}
	public Integer getNumPrecRadix() {
		return numPrecRadix.getInteger();
	}
	public Integer getNullable() {
		return nullable.getInteger();
	}
	public String getRemarks() {
		return remarks.getString();
	}
	public String getColumnDefault() {
		return columnDefault.getString();
	}
	public Integer getCharOctetLength() {
		return charOctetLength.getInteger();
	}
	public Integer getOrdinalPosition() {
		return ordinalPosition.getInteger();
	}
	public String getIsoNullable() {
		return isoNullable.getString();
	}
	public String getScopeCatalog() {
		return scopeCatalog.getString();
	}
	public String getScopeSchema() {
		return scopeSchema.getString();
	}
	public String getScopeTable() {
		return scopeTable.getString();
	}
	public Short getSourceDataType() {
		return sourceDataType.getShort();
	}
	public String getAutoincrement() {
		return autoincrement.getString();
	}
	public String getGeneratedcolumn() {
		return generatedcolumn.getString();
	}
	
	/**
	 * Use a DataType factory to create a DataType instance of the type appropriate
	 * to the metadata. 
	 * 
	 * TODO It would be good to have an equivalent factory method defined in IDataTypeFactory
	 * but that would impact third-party implementations of the interface (breaking change), so 
	 * could only be done as part of a major version release.  
	 * 
	 * @param dataTypeFactory
	 * @return DataType object as created by the factory
	 * @throws DataTypeException
	 */
	public DataType getDataType(IDataTypeFactory dataTypeFactory) throws DataTypeException {
        return dataTypeFactory.createDataType(
        		this.getSQLType(), 
        		this.getSqlTypeName(), 
        		this.getTableName(), 
        		this.getColumnName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autoincrement == null) ? 0 : autoincrement.hashCode());
		result = prime * result + ((charOctetLength == null) ? 0 : charOctetLength.hashCode());
		result = prime * result + ((columnDefault == null) ? 0 : columnDefault.hashCode());
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((columnSize == null) ? 0 : columnSize.hashCode());
		result = prime * result + ((decimalDigits == null) ? 0 : decimalDigits.hashCode());
		result = prime * result + ((generatedcolumn == null) ? 0 : generatedcolumn.hashCode());
		result = prime * result + ((isoNullable == null) ? 0 : isoNullable.hashCode());
		result = prime * result + ((nullable == null) ? 0 : nullable.hashCode());
		result = prime * result + ((numPrecRadix == null) ? 0 : numPrecRadix.hashCode());
		result = prime * result + ((ordinalPosition == null) ? 0 : ordinalPosition.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((scopeCatalog == null) ? 0 : scopeCatalog.hashCode());
		result = prime * result + ((scopeSchema == null) ? 0 : scopeSchema.hashCode());
		result = prime * result + ((scopeTable == null) ? 0 : scopeTable.hashCode());
		result = prime * result + ((sourceDataType == null) ? 0 : sourceDataType.hashCode());
		result = prime * result + ((sqlType == null) ? 0 : sqlType.hashCode());
		result = prime * result + ((sqlTypeName == null) ? 0 : sqlTypeName.hashCode());
		result = prime * result + ((tableCat == null) ? 0 : tableCat.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((tableSchema == null) ? 0 : tableSchema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnMetaData other = (ColumnMetaData) obj;
		if (autoincrement == null) {
			if (other.autoincrement != null)
				return false;
		} else if (!autoincrement.equals(other.autoincrement))
			return false;
		if (charOctetLength == null) {
			if (other.charOctetLength != null)
				return false;
		} else if (!charOctetLength.equals(other.charOctetLength))
			return false;
		if (columnDefault == null) {
			if (other.columnDefault != null)
				return false;
		} else if (!columnDefault.equals(other.columnDefault))
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (columnSize == null) {
			if (other.columnSize != null)
				return false;
		} else if (!columnSize.equals(other.columnSize))
			return false;
		if (decimalDigits == null) {
			if (other.decimalDigits != null)
				return false;
		} else if (!decimalDigits.equals(other.decimalDigits))
			return false;
		if (generatedcolumn == null) {
			if (other.generatedcolumn != null)
				return false;
		} else if (!generatedcolumn.equals(other.generatedcolumn))
			return false;
		if (isoNullable == null) {
			if (other.isoNullable != null)
				return false;
		} else if (!isoNullable.equals(other.isoNullable))
			return false;
		if (nullable == null) {
			if (other.nullable != null)
				return false;
		} else if (!nullable.equals(other.nullable))
			return false;
		if (numPrecRadix == null) {
			if (other.numPrecRadix != null)
				return false;
		} else if (!numPrecRadix.equals(other.numPrecRadix))
			return false;
		if (ordinalPosition == null) {
			if (other.ordinalPosition != null)
				return false;
		} else if (!ordinalPosition.equals(other.ordinalPosition))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (scopeCatalog == null) {
			if (other.scopeCatalog != null)
				return false;
		} else if (!scopeCatalog.equals(other.scopeCatalog))
			return false;
		if (scopeSchema == null) {
			if (other.scopeSchema != null)
				return false;
		} else if (!scopeSchema.equals(other.scopeSchema))
			return false;
		if (scopeTable == null) {
			if (other.scopeTable != null)
				return false;
		} else if (!scopeTable.equals(other.scopeTable))
			return false;
		if (sourceDataType == null) {
			if (other.sourceDataType != null)
				return false;
		} else if (!sourceDataType.equals(other.sourceDataType))
			return false;
		if (sqlType == null) {
			if (other.sqlType != null)
				return false;
		} else if (!sqlType.equals(other.sqlType))
			return false;
		if (sqlTypeName == null) {
			if (other.sqlTypeName != null)
				return false;
		} else if (!sqlTypeName.equals(other.sqlTypeName))
			return false;
		if (tableCat == null) {
			if (other.tableCat != null)
				return false;
		} else if (!tableCat.equals(other.tableCat))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (tableSchema == null) {
			if (other.tableSchema != null)
				return false;
		} else if (!tableSchema.equals(other.tableSchema))
			return false;
		return true;
	}



	private abstract class MetaDataField {
		String name;
		int index;
		
		protected MetaDataField(String name, int index) {
			super();
			this.name = name;
			this.index = index;
			// Every instance created must be added to the parent object's list of fields
			ColumnMetaData.this.fields.add(this);
		}

		protected void setValue(ResultSet resultSet) throws SQLException {
			if (resultSet.getMetaData().getColumnCount() >= this.index) {
				Object val = resultSet.getObject(this.index);
				// Skip nulls
				if (val != null) {
					setCheckedValue(resultSet);
				}
			}
		}
		
		// Having verified that a value for this field is present in the result set, assign it
		abstract void setCheckedValue(ResultSet resultSet) throws SQLException;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetaDataField other = (MetaDataField) obj;
			if (index != other.index)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
	}
	
	private class StringMetaDataField extends MetaDataField {
		String value;

		StringMetaDataField(String name, int index) {
			super(name, index);
		}
		
		void setCheckedValue(ResultSet resultSet) throws SQLException{
			this.value = resultSet.getString(this.index);
		}
		
		String getString() {
			return this.value;
		}

		@Override
		public String toString() {
			return "StringMetaDataField [name=" + name + ", index=" + index + ", value=" + value + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			StringMetaDataField other = (StringMetaDataField) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}
	
	private class IntegerMetaDataField extends MetaDataField {
		Integer value;

		IntegerMetaDataField(String name, int index) {
			super(name, index);
		}
		
		void setCheckedValue(ResultSet resultSet) throws SQLException{
			this.value = resultSet.getInt(this.index);
		}
		
		Integer getInteger() {
			return this.value;
		}

		@Override
		public String toString() {
			return "IntegerMetaDataField [name=" + name + ", index=" + index + ", value=" + value + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntegerMetaDataField other = (IntegerMetaDataField) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
	}
	
	private class ShortMetaDataField extends MetaDataField {
		Short value;

		ShortMetaDataField(String name, int index) {
			super(name, index);
		}
		
		void setCheckedValue(ResultSet resultSet) throws SQLException{
			this.value = resultSet.getShort(this.index);
		}
		
		Short getShort() {
			return this.value;
		}

		@Override
		public String toString() {
			return "ShortMetaDataField [name=" + name + ", index=" + index + ", value=" + value + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			ShortMetaDataField other = (ShortMetaDataField) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

}
