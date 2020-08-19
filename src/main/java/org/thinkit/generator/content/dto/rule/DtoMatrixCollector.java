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

import com.google.common.flogger.FluentLogger;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.common.util.workbook.FluentSheet;
import org.thinkit.common.util.workbook.FluentWorkbook;
import org.thinkit.framework.content.rule.Rule;
import org.thinkit.framework.content.rule.RuleInvoker;
import org.thinkit.generator.common.vo.dto.DtoMatrix;
import org.thinkit.generator.workbook.common.Sheet;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Excelに記述された定義書シートからクラス定義情報を読み取る処理を定義したルールクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
final class DtoMatrixCollector implements Rule<DtoMatrix> {

    /**
     * ログ出力オブジェクト
     */
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    /**
     * ファイルパス
     */
    private String filePath;

    /**
     * デフォルトコンストラクタ
     */
    private DtoMatrixCollector() {
    }

    /**
     * コンストラクタ
     *
     * @param filePath DTO定義書のファイルパス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    private DtoMatrixCollector(@NonNull String filePath) {

        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("wrong parameter was given. File path is required.");
        }

        this.filePath = filePath;
    }

    /**
     * 引数として指定された定義書へのファイルパスを基に {@link DtoMatrixCollector} クラスの新しいインスタンスを生成し返却します。
     *
     * @param filePath DTO定義書へのパス
     * @return {@link DtoMatrixCollector} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    public static Rule<DtoMatrix> from(@NonNull String filePath) {
        return new DtoMatrixCollector(filePath);
    }

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

    @Override
    public DtoMatrix execute() {

        final FluentWorkbook workbook = FluentWorkbook.builder().fromFile(this.filePath).build();
        final FluentSheet sheet = workbook.sheet(SheetName.定義書.name());

        final DtoMatrix dtoMatrix = DtoMatrix.of(RuleInvoker.of(DtoMetaCollector.from(sheet)).invoke(),
                RuleInvoker.of(DtoCreatorCollector.from(sheet)).invoke(),
                RuleInvoker.of(DtoDefinitionCollector.from(sheet)).invoke());

        logger.atFinest().log("DTOマトリクス = (%s)", dtoMatrix);
        return dtoMatrix;
    }
}
