package org.apache.atlas.demo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.TypesDef;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.types.AttributeDefinition;
import org.apache.atlas.typesystem.types.ClassType;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.atlas.typesystem.types.EnumTypeDefinition;
import org.apache.atlas.typesystem.types.HierarchicalTypeDefinition;
import org.apache.atlas.typesystem.types.Multiplicity;
import org.apache.atlas.typesystem.types.StructTypeDefinition;
import org.apache.atlas.typesystem.types.TraitType;
import org.apache.atlas.typesystem.types.utils.TypesUtil;

import java.util.List;

public class AtlasTypesDemo {

    public static final String HBASE_NAMESPACE_TYPE = "hbase_namespace";
    public static final String HBASE_TABLE_TYPE = "hbase_table";
    public static final String ASSET_TYPE = "Asset";
    public static final String HBASE_COLUMN_TYPE = "hbase_column";
    public static final String HBASE_COLUMN_FAMILY_TYPE = "hbase_column_family";
    private final AtlasClient atlasClient;

    public AtlasTypesDemo(String atlasServiceUrl) {
        atlasClient = new AtlasClient(new String[]{atlasServiceUrl}, new String[]{"admin", "admin"});
    }

    public static void main(String[] args) throws AtlasServiceException {
        AtlasTypesDemo atlasTypesDemo = new AtlasTypesDemo(args[0]);
        atlasTypesDemo.run();
    }

    private void run() throws AtlasServiceException {

        HierarchicalTypeDefinition<ClassType> namespaceType =
                TypesUtil.createClassTypeDef(HBASE_NAMESPACE_TYPE, ImmutableSet.of(ASSET_TYPE));
        HierarchicalTypeDefinition<ClassType> columnType =
                TypesUtil.createClassTypeDef(HBASE_COLUMN_TYPE, ImmutableSet.of(ASSET_TYPE),
                    new AttributeDefinition("type", DataTypes.STRING_TYPE.getName(), Multiplicity.REQUIRED, false, null));
        HierarchicalTypeDefinition<ClassType> columnFamilyType =
                TypesUtil.createClassTypeDef(HBASE_COLUMN_FAMILY_TYPE, ImmutableSet.of(ASSET_TYPE),
                    new AttributeDefinition("versions", DataTypes.INT_TYPE.getName(), Multiplicity.OPTIONAL, false, null),
                    new AttributeDefinition("inMemory", DataTypes.BOOLEAN_TYPE.getName(), Multiplicity.OPTIONAL, false, null),
                    new AttributeDefinition("blockSize", DataTypes.INT_TYPE.getName(), Multiplicity.REQUIRED, false, null),
                    new AttributeDefinition("compression", DataTypes.STRING_TYPE.getName(), Multiplicity.OPTIONAL, false, null),
                    new AttributeDefinition("columns", DataTypes.arrayTypeName(HBASE_COLUMN_TYPE), Multiplicity.COLLECTION, false, null));
        HierarchicalTypeDefinition<ClassType> tableType =
                TypesUtil.createClassTypeDef(HBASE_TABLE_TYPE, ImmutableSet.of("DataSet"),
                    new AttributeDefinition("namespace", HBASE_NAMESPACE_TYPE, Multiplicity.REQUIRED, false, null),
                    new AttributeDefinition("status", DataTypes.BOOLEAN_TYPE.getName(), Multiplicity.OPTIONAL, false, null),
                    new AttributeDefinition("columnFamilies", DataTypes.arrayTypeName(HBASE_COLUMN_FAMILY_TYPE), Multiplicity.COLLECTION, true, null));
        TypesDef typesDef = TypesUtil.getTypesDef(ImmutableList.<EnumTypeDefinition>of(), ImmutableList.<StructTypeDefinition>of(),
                ImmutableList.<HierarchicalTypeDefinition<TraitType>>of(),
                ImmutableList.of(namespaceType, columnType, columnFamilyType, tableType));
        String typesAsString = TypesSerialization.toJson(typesDef);
        System.out.println(typesAsString);
    }
}
