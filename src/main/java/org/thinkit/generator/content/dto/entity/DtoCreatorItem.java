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

package org.thinkit.generator.content.dto.entity;

import java.io.Serializable;

import org.thinkit.framework.content.entity.ContentEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * コンテンツ「DTO作成者項目」の情報を管理するデータクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@Getter
@ToString
@EqualsAndHashCode
public final class DtoCreatorItem implements ContentEntity, Serializable {

    /**
     * シリアルバージョンUID
     */
    private static final long serialVersionUID = 8717094695982939096L;

    /**
     * セル項目コード
     */
    private int cellItemCode;

    /**
     * セル項目名
     */
    private String cellItemName;

    /**
     * デフォルトコンストラクタ
     */
    private DtoCreatorItem() {
    }

    /**
     * コンストラクタ
     *
     * @param cellItemCode セル項目コード
     * @param cellItemName セル項目名
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoCreatorItem(int cellItemCode, @NonNull String cellItemName) {
        this.cellItemCode = cellItemCode;
        this.cellItemName = cellItemName;
    }

    /**
     * コピーコンストラクタ
     *
     * @param dtoCreatorItem DTO作成者項目
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoCreatorItem(@NonNull DtoCreatorItem dtoCreatorItem) {
        this.cellItemCode = dtoCreatorItem.getCellItemCode();
        this.cellItemName = dtoCreatorItem.getCellItemName();
    }

    /**
     * 引数として渡された情報を基に {@link DtoCreatorItem} クラスの新しいインスタンスを生成し返却します。
     *
     * @param cellItemCode セル項目コード
     * @param cellItemName セル項目名
     * @return {@link DtoCreatorItem} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public static DtoCreatorItem of(int cellItemCode, @NonNull String cellItemName) {
        return new DtoCreatorItem(cellItemCode, cellItemName);
    }

    /**
     * 引数として渡された {@code dtoCreatorItem} オブジェクトの情報を基に {@link DtoCreatorItem}
     * クラスの新しいインスタンスを生成し返却します。
     *
     * @param dtoCreatorItem DTO作成者項目
     * @return {@link DtoCreatorItem} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public static DtoCreatorItem of(@NonNull DtoCreatorItem dtoCreatorItem) {
        return new DtoCreatorItem(dtoCreatorItem);
    }
}