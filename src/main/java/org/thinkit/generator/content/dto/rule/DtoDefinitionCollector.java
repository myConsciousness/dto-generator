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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.flogger.FluentLogger;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.common.util.workbook.FluentSheet;
import org.thinkit.common.util.workbook.Matrix;
import org.thinkit.framework.content.ContentInvoker;
import org.thinkit.framework.content.rule.Rule;
import org.thinkit.generator.common.catalog.dto.DtoItem;
import org.thinkit.generator.common.vo.dto.DtoDefinition;
import org.thinkit.generator.common.vo.dto.DtoDefinitionGroup;
import org.thinkit.generator.common.vo.dto.DtoField;
import org.thinkit.generator.common.vo.dto.DtoFieldGroup;
import org.thinkit.generator.content.dto.DtoDefinitionItemLoader;
import org.thinkit.generator.content.dto.entity.DtoDefinitionItem;
import org.thinkit.generator.content.dto.entity.DtoDefinitionItemGroup;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Excelに記載されたDTO定義のクラス本体部分の情報を読み取る処理を定義したルールクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
final class DtoDefinitionCollector implements Rule<DtoDefinitionGroup> {

    /**
     * ログ出力オブジェクト
     */
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    /**
     * 再帰処理開始時のインデックス
     */
    private static final int RECURSIVE_START_INDEX = 0;

    /**
     * 再帰処理開始時の基準層
     */
    private static final int RECURSIVE_BASE_LAYER = 1;

    /**
     * 操作対象のシートオブジェクト
     */
    private FluentSheet sheet;

    /**
     * デフォルトコンストラクタ
     */
    private DtoDefinitionCollector() {
    }

    /**
     * コンストラクタ
     *
     * @param sheet 操作する対象のシートオブジェクト
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoDefinitionCollector(@NonNull FluentSheet sheet) {
        this.sheet = sheet;
    }

    /**
     * 引数として渡された {@code sheet} を基に {@link DtoDefinitionCollector}
     * クラスの新しいインスタンスを生成し返却します。
     *
     * @param sheet 操作する対象のシートオブジェクト
     * @return {@link DtoDefinitionCollector} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @see FluentSheet
     */
    public static Rule<DtoDefinitionGroup> from(@NonNull FluentSheet sheet) {
        return new DtoDefinitionCollector(sheet);
    }

    @Override
    public DtoDefinitionGroup execute() {

        final DtoDefinitionGroup dtoDefinitionGroup = this.getDtoDefinitionList(this.sheet);

        if (dtoDefinitionGroup.isEmpty()) {
            logger.atSevere().log("DTO定義グループを取得できませんでした。");
            return null;
        }

        return dtoDefinitionGroup;
    }

    /**
     * Excelに定義されたマトリクステーブルからDTO定義情報群を取得し返却します。
     *
     * @param sheet Sheetオブジェクト
     * @return DTO定義情報群
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoDefinitionGroup getDtoDefinitionList(@NonNull FluentSheet sheet) {

        final DtoDefinitionItemGroup dtoDefinitionItemGroup = ContentInvoker.of(DtoDefinitionItemLoader.of()).invoke();

        final String baseCellItem = this.getItemName(dtoDefinitionItemGroup, DtoItem.LOGICAL_DELETE);
        final Matrix baseIndexes = sheet.findCellIndex(baseCellItem);

        final List<Map<String, String>> matrixList = sheet.getMatrixList(baseIndexes.getColumn(), baseIndexes.getRow());
        logger.atFinest().log("マトリクスリスト = (%s)", matrixList);

        final DtoDefinitionGroup dtoDefinitionGroup = DtoDefinitionGroup.of();

        this.craeteDtoDefinitionRecursively(RecursiveRequiredParameters.of(matrixList, dtoDefinitionItemGroup,
                dtoDefinitionGroup, RECURSIVE_START_INDEX, RECURSIVE_BASE_LAYER));

        logger.atFinest().log("DTO定義グループ = (%s)", dtoDefinitionGroup);
        return dtoDefinitionGroup;
    }

    /**
     * 引数として指定されたマトリクスリストから再帰的にDTO定義情報群を生成します。<br>
     * 再帰処理は各レコードが子クラスを持っている場合に実行されます。
     *
     * @param recursiveRequiredParameters 再帰処理時に必須となる情報を格納したデータクラス
     * @return 子クラスを生成する際に使用したレコード数
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     *
     * @see RecursiveRequiredParameters
     */
    private int craeteDtoDefinitionRecursively(@NonNull final RecursiveRequiredParameters recursiveRequiredParameters) {

        final List<Map<String, String>> matrixList = recursiveRequiredParameters.getMatrixList();
        final DtoDefinitionItemGroup dtoDefinitionItemGroup = recursiveRequiredParameters.getDtoDefinitionItemGroup();
        final DtoDefinitionGroup dtoDefinitionGroup = recursiveRequiredParameters.getDtoDefinitionGroup();
        final int startIndex = recursiveRequiredParameters.getStartIndex();
        final int baseItemLayer = recursiveRequiredParameters.getBaseItemLayer();

        logger.atFinest().log("開始インデックス = (%s)", startIndex);
        logger.atFinest().log("基準項目層 = (%s)", baseItemLayer);

        DtoDefinition parentDtoDefinition = new DtoDefinition();
        DtoFieldGroup dtoFieldGroup = DtoFieldGroup.of();

        int recordCounter = 0;
        for (int i = startIndex, size = matrixList.size(); i < size; i++) {
            final Map<String, String> record = matrixList.get(i);

            final boolean deleted = this.convertStringToBoolean(
                    record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.LOGICAL_DELETE)));

            if (deleted) {
                logger.atFinest().log("論理削除されたレコードのためスキップします。");
                logger.atFinest().log("スキップされたレコード = (%s)", record);
                recordCounter++;
                continue;
            }

            final int layer = Integer.parseInt(record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.LAYER)));
            logger.atFinest().log("レコードから取得した項目層 = (%s)", layer);

            if (layer + 1 < baseItemLayer) {
                logger.atFinest().log("%s層の処理を終了します。", baseItemLayer);
                logger.atFinest().log("戻り先の層 = (%s)", baseItemLayer - 2);
                break;
            }

            if (layer == baseItemLayer - 1 && layer % 2 == 0) {
                parentDtoDefinition = new DtoDefinition();
                dtoFieldGroup = DtoFieldGroup.of();

                parentDtoDefinition.setDtoFieldGroup(dtoFieldGroup);
                dtoDefinitionGroup.add(parentDtoDefinition);

                this.createDtoDefinition(dtoDefinitionItemGroup, record, parentDtoDefinition);
            } else {
                if (layer > baseItemLayer) {
                    logger.atFinest().log("子クラス情報を生成するため再帰処理を開始します。");

                    final DtoDefinitionGroup childDtoDefinitionGroup = DtoDefinitionGroup.of();
                    final int skipCounter = this.craeteDtoDefinitionRecursively(RecursiveRequiredParameters
                            .of(matrixList, dtoDefinitionItemGroup, childDtoDefinitionGroup, i, baseItemLayer + 2));

                    dtoFieldGroup.get(dtoFieldGroup.size() - 1).setChildDtoDefinitionGroup(childDtoDefinitionGroup);

                    logger.atFinest().log("レコード番号 = (%s)", i);
                    logger.atFinest().log("スキップ数 = (%s)", skipCounter);
                    i += skipCounter - 1;
                } else {
                    this.createDtoField(dtoDefinitionItemGroup, record, dtoFieldGroup);
                }
            }

            recordCounter++;
        }

        logger.atFinest().log("DTO定義グループ（途中経過） = (%s)", dtoDefinitionGroup);
        return recordCounter;
    }

    /**
     * マトリクスから取得したレコードを基にDTO定義情報を生成します。
     *
     * @param dtoDefinitionItemGroup DTO定期項目グループ
     * @param record                 マトリクスレコード
     * @param dtoDefinition          DTO定義情報
     */
    private void createDtoDefinition(final DtoDefinitionItemGroup dtoDefinitionItemGroup,
            final Map<String, String> record, final DtoDefinition dtoDefinition) {

        final String className = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.VARIABLE_NAME));
        final String description = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.DESCRIPTION));

        dtoDefinition.setClassName(className);
        dtoDefinition.setDescription(description);

        logger.atFinest().log("DTO定義 = (%s)", dtoDefinition.toString());
    }

    /**
     * マトリクスから取得した情報を基にクラス項目情報を生成します。
     *
     * @param dtoDefinitionItemGroup DTO定義項目グループ
     * @param record                 マトリクスレコード
     * @param dtoFieldList           DTOフィールドリスト
     */
    private void createDtoField(final DtoDefinitionItemGroup dtoDefinitionItemGroup, Map<String, String> record,
            DtoFieldGroup dtoFieldGroup) {

        final String variableName = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.VARIABLE_NAME));
        final String dataType = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.DATA_TYPE));
        final String initialValue = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.INITIAL_VALUE));
        final boolean invariant = this
                .convertStringToBoolean(record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.INVARIANT)));
        final String description = record.get(this.getItemName(dtoDefinitionItemGroup, DtoItem.DESCRIPTION));

        final DtoField classItemDefinition = DtoField.of(variableName, dataType, initialValue, invariant, description);

        dtoFieldGroup.add(classItemDefinition);

        logger.atFinest().log("DTOフィールド = (%s)", classItemDefinition);
    }

    /**
     * 文字列を真偽値に変換します。
     * <p>
     * 真偽値へ変換する際のルールは下記の通りです。<br>
     * 当該メソッドでは文字列に対するトリム加工は行いません。
     * <p>
     * 1, 文字列がnullの場合: {@code false} <br>
     * 2, 文字列が空文字列の場合: {@code false} <br>
     * 3, 上記以外の場合: {@code true} <br>
     *
     * @param sequence 変換対象の文字列
     * @return 文字列がnullまたは空文字列の場合は {@code false} 、それ以外は {@code true}
     */
    private boolean convertStringToBoolean(final String sequence) {
        return !StringUtils.isEmpty(sequence);
    }

    /**
     * 指定されたセル項目に紐づく名称を取得し返却します。
     * <p>
     * 指定されたセル項目に紐づく名称が存在しない場合は空文字列を返却します。
     *
     * @param dtoDefinitionItemGroup DTO定義項目グループ
     * @param dtoItem                取得対象のセル項目
     * @return 取得対象のセル項目に紐づく名称、または、セル項目に紐づく名称が存在しない場合は空文字列
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private String getItemName(@NonNull final DtoDefinitionItemGroup dtoDefinitionItemGroup, DtoItem dtoItem) {
        return dtoDefinitionItemGroup.stream()
                .filter(dtoDefinitionItem -> dtoDefinitionItem.getCellItemCode() == dtoItem.getCode())
                .map(DtoDefinitionItem::getCellItemName).findFirst().orElse(StringUtils.EMPTY);
    }

    /**
     * DTO定義情報を取得する際の再帰処理で必要となるパラメータ情報を管理するデータクラスです。
     *
     * @author Kato Shinya
     * @since 1.0
     * @version 1.0
     */
    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor(staticName = "of")
    private static class RecursiveRequiredParameters implements Serializable {

        /**
         * シリアルバージョンUID
         */
        private static final long serialVersionUID = 3989477460378492335L;

        /**
         * Excelの定義書シートから取得したマトリクスの生データリスト
         */
        @NonNull
        private final List<Map<String, String>> matrixList;

        /**
         * DTO定義項目グループ
         */
        @NonNull
        private final DtoDefinitionItemGroup dtoDefinitionItemGroup;

        /**
         * DTO定義グループ
         */
        @NonNull
        private final DtoDefinitionGroup dtoDefinitionGroup;

        /**
         * 探索開始インデックス
         */
        private final int startIndex;

        /**
         * 基準項目層
         */
        private final int baseItemLayer;
    }
}