package org.thinkit.generator.command.dtogenerator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.flogger.FluentLogger;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.common.command.Command;
import org.thinkit.common.rule.Attribute;
import org.thinkit.common.rule.RuleInvoker;
import org.thinkit.common.util.workbook.FluentSheet;
import org.thinkit.common.util.workbook.FluentWorkbook;
import org.thinkit.common.util.workbook.Matrix;
import org.thinkit.generator.command.Sheet;
import org.thinkit.generator.common.catalog.dtogenerator.DtoItem;
import org.thinkit.generator.common.dto.dtogenerator.ClassNameDefinition;
import org.thinkit.generator.rule.dtogenerator.ClassNameDefinitionLoader;
import org.thinkit.common.catalog.Catalog;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
final class ClassNameDefinitionCollector implements Command<ClassNameDefinition> {

    /**
     * ログ出力オブジェクト
     */
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    /**
     * 操作対象のシートオブジェクト
     */
    private FluentSheet sheet;

    /**
     * ファイルパス
     */
    private String filePath;

    /**
     * シート名定数
     */
    private enum SheetName implements Sheet {
        定義書;

        @Override
        public String getString() {
            return this.name();
        }
    }

    /**
     * コンテンツ要素定数
     */
    private enum ContentAttribute implements Attribute {
        セル項目コード, セル項目名;

        @Override
        public String getString() {
            return this.name();
        }
    }

    /**
     * コンストラクタ
     *
     * @param filePath DTO定義書のファイルパス
     * @exception IllegalArgumentException ファイルパスがnullまたは空文字列の場合
     */
    public ClassNameDefinitionCollector(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("wrong parameter was given. File path is required.");
        }

        this.filePath = filePath;
    }

    /**
     * コンストラクタ
     *
     * @param sheet DTO定義書の情報を持つSheetオブジェクト
     */
    public ClassNameDefinitionCollector(@NonNull FluentSheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public ClassNameDefinition run() {

        if (this.sheet == null) {
            final FluentWorkbook workbook = new FluentWorkbook.Builder().fromFile(this.filePath).build();
            this.sheet = workbook.sheet(SheetName.定義書.name());
        }

        final Map<DtoItem, String> definitions = this.getNameDefinitions(sheet);
        final ClassNameDefinition classNameDefinition = new ClassNameDefinition(definitions.get(DtoItem.VERSION),
                definitions.get(DtoItem.PROJECT_NAME), definitions.get(DtoItem.PACKAGE_NAME),
                definitions.get(DtoItem.PHYSICAL_NAME), definitions.get(DtoItem.LOGICAL_NAME),
                definitions.get(DtoItem.DESCRIPTION));

        logger.atInfo().log("クラス名定義情報 = (%s)", classNameDefinition);
        return classNameDefinition;
    }

    /**
     * セル内に定義された作成者情報を取得し返却します。
     *
     * @param sheet Sheetオブジェクト
     * @return セルに定義されたクラス名情報
     */
    private EnumMap<DtoItem, String> getNameDefinitions(FluentSheet sheet) {

        final List<Map<String, String>> contents = RuleInvoker.of(new ClassNameDefinitionLoader()).invoke();
        final EnumMap<DtoItem, String> classNameDefinitions = new EnumMap<>(DtoItem.class);

        for (Map<String, String> elements : contents) {
            final String cellItemName = elements.get(ContentAttribute.セル項目名.name());
            final Matrix baseIndexes = sheet.findCellIndex(cellItemName);

            final String sequence = sheet.getRegionSequence(baseIndexes.getColumn(), baseIndexes.getRow());
            logger.atInfo().log("取得した領域内の値 = (%s)", sequence);

            final int itemCode = Integer.parseInt(elements.get(ContentAttribute.セル項目コード.name()));
            classNameDefinitions.put(Catalog.getEnum(DtoItem.class, itemCode), sequence);
        }

        logger.atInfo().log("コンテンツ情報 = (%s)", classNameDefinitions);
        return classNameDefinitions;
    }
}