/**
 * Project Name : Generator<br>
 * File Name : DtoCreatorCollector.java<br>
 * Encoding : UTF-8<br>
 * Creation Date : 2020/06/13<br>
 * <p>
 * Copyright © 2020 Kato Shinya. All rights reserved.
 * <p>
 * This source code or any portion thereof must not be<br>
 * reproduced or used in any manner whatsoever.
 */

package org.thinkit.generator.command.dto;

import java.util.EnumMap;

import com.google.common.flogger.FluentLogger;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.common.catalog.Catalog;
import org.thinkit.common.command.Command;
import org.thinkit.common.rule.RuleInvoker;
import org.thinkit.common.util.workbook.FluentSheet;
import org.thinkit.common.util.workbook.FluentWorkbook;
import org.thinkit.common.util.workbook.Matrix;
import org.thinkit.generator.command.Sheet;
import org.thinkit.generator.common.catalog.dto.DtoItem;
import org.thinkit.generator.common.vo.dto.DtoCreator;
import org.thinkit.generator.rule.dto.DtoCreatorItemLoader;
import org.thinkit.generator.vo.dto.DtoCreatorItemGroup;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Excelに記載されたDTO定義の作成者部分の情報を読み取る処理を定義したコマンドクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
final class DtoCreatorCollector implements Command<DtoCreator> {

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
     * コンストラクタ
     *
     * @param filePath DTO定義書のファイルパス
     * @exception IllegalArgumentException ファイルパスがnullまたは空文字列の場合
     */
    public DtoCreatorCollector(String filePath) {

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
    public DtoCreatorCollector(@NonNull FluentSheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public DtoCreator run() {

        if (this.sheet == null) {
            final FluentWorkbook workbook = FluentWorkbook.builder().fromFile(this.filePath).build();
            this.sheet = workbook.sheet(SheetName.定義書.name());
        }

        final EnumMap<DtoItem, String> dtoCreator = this.getDtoCreator(this.sheet);
        final DtoCreator classCreatorDefinition = new DtoCreator(dtoCreator.get(DtoItem.CREATOR),
                dtoCreator.get(DtoItem.CREATION_TIME), dtoCreator.get(DtoItem.UPDTATE_TIME));

        logger.atInfo().log("クラス作成者情報 = (%s)", classCreatorDefinition);
        return classCreatorDefinition;
    }

    /**
     * セル内に定義された作成者情報を取得し返却します。
     *
     * @param sheet Sheetオブジェクト
     * @return セルに定義された作成者情報
     */
    private EnumMap<DtoItem, String> getDtoCreator(FluentSheet sheet) {

        final DtoCreatorItemGroup dtoCreatorItemGroup = RuleInvoker.of(DtoCreatorItemLoader.of()).invoke();
        final EnumMap<DtoItem, String> dtoCreator = new EnumMap<>(DtoItem.class);

        dtoCreatorItemGroup.forEach(dtoCreatorItem -> {
            final Matrix baseIndexes = sheet.findCellIndex(dtoCreatorItem.getCellItemName());
            final String sequence = sheet.getRegionSequence(baseIndexes.getColumn(), baseIndexes.getRow());
            dtoCreator.put(Catalog.getEnum(DtoItem.class, dtoCreatorItem.getCellItemCode()), sequence);
        });

        logger.atInfo().log("コンテンツ情報 = (%s)", dtoCreator);
        return dtoCreator;
    }
}