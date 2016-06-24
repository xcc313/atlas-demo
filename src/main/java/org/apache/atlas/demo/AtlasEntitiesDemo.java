package org.apache.atlas.demo;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class AtlasEntitiesDemo implements AtlasDemoConstants {

    private final AtlasClient atlasClient;

    public AtlasEntitiesDemo(String atlasServiceUrl) {
        atlasClient = new AtlasClient(new String[]{atlasServiceUrl}, new String[]{"admin", "admin"});
    }

    public static void main(String[] args) throws AtlasServiceException, JSONException {
        AtlasEntitiesDemo atlasEntitiesDemo = new AtlasEntitiesDemo(args[0]);
        atlasEntitiesDemo.run();
    }

    private void run() throws AtlasServiceException, JSONException {
        String namespaceId = createNamespace();
        createTable(namespaceId);
    }

    private void createTable(String namespaceId) throws AtlasServiceException, JSONException {
        Referenceable cssNsiColumn = new Referenceable(HBASE_COLUMN_TYPE);
        cssNsiColumn.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable.anchor.cssnsi@cluster");
        cssNsiColumn.set(AtlasClient.NAME, "cssnsi");
        cssNsiColumn.set(AtlasClient.OWNER, "crawler");
        cssNsiColumn.set(COLUMN_ATTRIBUTE_TYPE, "string");

        Referenceable myLookCaColumn = new Referenceable(HBASE_COLUMN_TYPE);
        myLookCaColumn.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable.anchor.mylookca@cluster");
        myLookCaColumn.set(AtlasClient.NAME, "mylookca");
        myLookCaColumn.set(AtlasClient.OWNER, "crawler");
        myLookCaColumn.set(COLUMN_ATTRIBUTE_TYPE, "string");

        Referenceable htmlColumn = new Referenceable(HBASE_COLUMN_TYPE);
        htmlColumn.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable.contents.html@cluster");
        htmlColumn.set(AtlasClient.NAME, "html");
        htmlColumn.set(AtlasClient.OWNER, "crawler");
        htmlColumn.set(COLUMN_ATTRIBUTE_TYPE, "byte[]");

        Referenceable anchorCf = new Referenceable(HBASE_COLUMN_FAMILY_TYPE);
        anchorCf.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable.anchor@cluster");
        anchorCf.set(AtlasClient.NAME, "anchor");
        anchorCf.set(AtlasClient.DESCRIPTION, "The anchor column family that stores all links");
        anchorCf.set(AtlasClient.OWNER, "crawler");
        anchorCf.set(CF_ATTRIBUTE_VERSIONS, 3);
        anchorCf.set(CF_ATTRIBUTE_IN_MEMORY, true);
        anchorCf.set(CF_ATTRIBUTE_COMPRESSION, "zip");
        anchorCf.set(CF_ATTRIBUTE_BLOCK_SIZE, 128);
        anchorCf.set(CF_ATTRIBUTE_COLUMNS, Arrays.asList(cssNsiColumn, myLookCaColumn));

        Referenceable contentsCf = new Referenceable(HBASE_COLUMN_FAMILY_TYPE);
        contentsCf.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable.contents@cluster");
        contentsCf.set(AtlasClient.NAME, "contents");
        contentsCf.set(AtlasClient.DESCRIPTION, "The contents column family that stores the crawled content");
        contentsCf.set(AtlasClient.OWNER, "crawler");
        contentsCf.set(CF_ATTRIBUTE_VERSIONS, 1);
        contentsCf.set(CF_ATTRIBUTE_IN_MEMORY, false);
        contentsCf.set(CF_ATTRIBUTE_COMPRESSION, "lzo");
        contentsCf.set(CF_ATTRIBUTE_BLOCK_SIZE, 1024);
        contentsCf.set(CF_ATTRIBUTE_COLUMNS, Arrays.asList(htmlColumn));

        Referenceable webTable = new Referenceable(HBASE_TABLE_TYPE);
        webTable.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default.webtable@cluster");
        webTable.set(AtlasClient.NAME, "webtable");
        webTable.set(AtlasClient.DESCRIPTION, "Table that stores crawled information");
        webTable.set(TABLE_ATTRIBUTE_STATUS, "enabled");
        webTable.set(TABLE_ATTRIBUTE_NAMESPACE, new Referenceable(namespaceId, HBASE_NAMESPACE_TYPE, null));
        webTable.set(TABLE_ATTRIBUTE_COLUMN_FAMILIES, Arrays.asList(anchorCf, contentsCf));

        List<Referenceable> entities =
                Arrays.asList(cssNsiColumn, myLookCaColumn, htmlColumn, anchorCf, contentsCf, webTable);

        JSONArray entitiesJson = new JSONArray(entities.size());

        for (Referenceable entity : entities) {
            String entityJson = InstanceSerialization.toJson(entity, true);
            System.out.println(entityJson);
            entitiesJson.put(entityJson);
        }

        List<String> entitiesCreated = atlasClient.createEntity(entities);
        for (String entity : entitiesCreated) {
            System.out.println("Entity created: " + entity);
        }
    }

    private String createNamespace() throws AtlasServiceException {
        Referenceable hbaseNamespace = new Referenceable(HBASE_NAMESPACE_TYPE);
        hbaseNamespace.set(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, "default@cluster1");
        hbaseNamespace.set(AtlasClient.NAME, "default");
        hbaseNamespace.set(AtlasClient.DESCRIPTION, "Default HBase namespace");
        hbaseNamespace.set(AtlasClient.OWNER, "hbase_admin");
        String hbaseNamespaceJson = InstanceSerialization.toJson(hbaseNamespace, true);
        System.out.println(hbaseNamespaceJson);

        List<String> entitiesCreated = atlasClient.createEntity(hbaseNamespace);
        for (String entity : entitiesCreated) {
            System.out.println("Entity created: " + entity);
        }
        return entitiesCreated.get(0);
    }
}
