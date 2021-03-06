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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.thinkit.common.util.iterator.FluentIterator;
import org.thinkit.common.util.iterator.IterableNode;
import org.thinkit.framework.content.entity.ContentEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * コンテンツ「DTO定義項目」のオブジェクトグループを管理するデータクラスです。
 * <p>
 * このクラスはFluentインターフェースの概念を応用し設計されています。<br>
 * そのため、以下のようなメソッドチェーンでの操作が可能です。
 *
 * <pre>
 * <code>
 * DtoDefinitionItemGroup dtoDefinitionItemGroup = DtoDefinitionItemGroup.of()
 *                                  .add(dtoDefinitionItem1)
 *                                  .add(dtoDefinitionItem2);
 * </code>
 * </pre>
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
public final class DtoDefinitionItemGroup
        implements ContentEntity, Iterable<DtoDefinitionItem>, IterableNode<DtoDefinitionItem>, Serializable {

    /**
     * シリアルバージョンUID
     */
    private static final long serialVersionUID = 5501189774410411268L;

    /**
     * DTO定義項目グループ
     */
    @Getter
    private List<DtoDefinitionItem> dtoDefinitionItemGroup;

    /**
     * DTO定義項目グループのサイズ
     */
    private int size;

    /**
     * デフォルトコンストラクタ
     */
    private DtoDefinitionItemGroup() {
        this.dtoDefinitionItemGroup = new ArrayList<>(0);
        this.size = 0;
    }

    /**
     * コピーコンストラクタ
     *
     * @param dtoDefinitionItemGroup DTO定義項目グループ
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoDefinitionItemGroup(@NonNull DtoDefinitionItemGroup dtoDefinitionItemGroup) {
        this.dtoDefinitionItemGroup = new ArrayList<>(dtoDefinitionItemGroup.getDtoDefinitionItemGroup());
    }

    /**
     * {@link DtoDefinitionItemGroup} クラスの新しいインスタンスを生成し返却します。
     *
     * @return {@link DtoDefinitionItemGroup} クラスの新しいインスタンス
     */
    public static DtoDefinitionItemGroup of() {
        return new DtoDefinitionItemGroup();
    }

    /**
     * 引数として渡された {@code dtoDefinitionItemGroup} オブジェクトの情報を基に
     * {@link DtoDefinitionItemGroup} クラスの新しいインスタンスを生成し返却します。
     *
     * @param dtoDefinitionItemGroup DTO定義項目グループ
     * @return {@link DtoDefinitionItemGroup} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public static DtoDefinitionItemGroup of(@NonNull DtoDefinitionItemGroup dtoDefinitionItemGroup) {
        return new DtoDefinitionItemGroup(dtoDefinitionItemGroup);
    }

    /**
     * 引数として渡された {@code dtoDefinitionItem} を条件リストへ追加します。
     * <p>
     * この {@link DtoDefinitionItemGroup#add(DtoDefinitionItem)}
     * メソッドは自分自身のインスタンスを返却するため以下のようなメソッドチェーンでの操作が可能です。
     *
     * <pre>
     * <code>
     * DtoDefinitionItemGroup dtoDefinitionItemGroup = DtoDefinitionItemGroup.of()
     *                                  .add(dtoDefinitionItem1)
     *                                  .add(dtoDefinitionItem2);
     * </code>
     * </pre>
     *
     * @param dtoDefinitionItem DTO定義項目
     * @return 自分自身のインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public DtoDefinitionItemGroup add(@NonNull DtoDefinitionItem dtoDefinitionItem) {
        this.dtoDefinitionItemGroup.add(dtoDefinitionItem);
        size++;

        return this;
    }

    /**
     * オブジェクトに含まれる情報が空か判定します。
     *
     * @return オブジェクトに含まれる情報がからの場合は {@code true} 、それ以外は {@code false}
     */
    public boolean isEmpty() {
        return this.size <= 0;
    }

    /**
     * {@link DtoDefinitionItem} を総称型として持つストリームを返却します。
     *
     * @return {@link DtoDefinitionItem} を総称型として持つストリーム
     */
    public Stream<DtoDefinitionItem> stream() {
        return this.dtoDefinitionItemGroup.stream();
    }

    @Override
    public List<DtoDefinitionItem> nodes() {
        return this.dtoDefinitionItemGroup;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<DtoDefinitionItem> iterator() {
        return FluentIterator.of(this);
    }
}