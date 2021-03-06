/*
 * Copyright 2020 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.thinkit.generator.content.dto.rule;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.flogger.FluentLogger;

import org.thinkit.common.catalog.Catalog;
import org.thinkit.common.util.workbook.FluentSheet;
import org.thinkit.common.util.workbook.Matrix;
import org.thinkit.framework.content.ContentInvoker;
import org.thinkit.framework.content.rule.Rule;
import org.thinkit.generator.common.catalog.dto.DtoItem;
import org.thinkit.generator.common.vo.dto.DtoCreator;
import org.thinkit.generator.content.dto.DtoCreatorItemLoader;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Excelに記載されたDTO定義の作成者部分の情報を読み取る処理を定義したルールクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
final class DtoCreatorCollector implements Rule<DtoCreator> {

    /**
     * ログ出力オブジェクト
     */
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    /**
     * 操作対象のシートオブジェクト
     */
    private FluentSheet sheet;

    /**
     * デフォルトコンストラクタ
     */
    private DtoCreatorCollector() {
    }

    /**
     * コンストラクタ
     *
     * @param sheet 操作する対象のシートオブジェクト
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoCreatorCollector(@NonNull FluentSheet sheet) {
        this.sheet = sheet;
    }

    /**
     * 引数として渡された {@code sheet} を基に {@link DtoCreatorCollector}
     * クラスの新しいインスタンスを生成し返却します。
     *
     * @param sheet 操作する対象のシートオブジェクト
     * @return {@link DtoCreatorCollector} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @see FluentSheet
     */
    public static Rule<DtoCreator> from(@NonNull FluentSheet sheet) {
        return new DtoCreatorCollector(sheet);
    }

    @Override
    public DtoCreator execute() {

        final Map<DtoItem, String> dtoCreator = this.getDtoCreator(this.sheet);

        return DtoCreator.of(dtoCreator.get(DtoItem.CREATOR), dtoCreator.get(DtoItem.CREATION_TIME),
                dtoCreator.get(DtoItem.UPDTATE_TIME));
    }

    /**
     * セル内に定義されたDTO作成者項目を取得し返却します。
     *
     * @param sheet Sheetオブジェクト
     * @return セルに定義されたDTO作成者項目
     */
    private Map<DtoItem, String> getDtoCreator(FluentSheet sheet) {

        final Map<DtoItem, String> dtoCreator = new EnumMap<>(DtoItem.class);

        ContentInvoker.of(DtoCreatorItemLoader.of()).invoke().forEach(dtoCreatorItem -> {
            final Matrix baseIndexes = sheet.findCellIndex(dtoCreatorItem.getCellItemName());
            final String sequence = sheet.getRegionSequence(baseIndexes.getColumn(), baseIndexes.getRow());
            dtoCreator.put(Catalog.getEnum(DtoItem.class, dtoCreatorItem.getCellItemCode()), sequence);
        });

        logger.atFinest().log("DTO作成者 = (%s)", dtoCreator);
        return dtoCreator;
    }
}