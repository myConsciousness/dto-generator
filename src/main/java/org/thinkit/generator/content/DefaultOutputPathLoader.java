/**
 * Project Name : Generator<br>
 * File Name : DefaultOutputPathLoader.java<br>
 * Encoding : UTF-8<br>
 * Creation Date : 2020/06/15<br>
 * <p>
 * Copyright © 2020 Kato Shinya. All rights reserved.
 * <p>
 * This source code or any portion thereof must not be<br>
 * reproduced or used in any manner whatsoever.
 */

package org.thinkit.generator.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thinkit.common.catalog.Platform;
import org.thinkit.framework.content.Attribute;
import org.thinkit.framework.content.Condition;
import org.thinkit.framework.content.Content;
import org.thinkit.framework.content.annotation.ContentMapping;
import org.thinkit.generator.content.entity.DefaultOutputPath;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 既定の出力先パスを管理するクラスです。
 * <p>
 * {@link #execute()} を実行することでコンテンツ「既定出力先」から既定の出力先を生成する際に必要な情報を取得します。
 * <p>
 * 実行の前提としてプログラム実行時のプラットフォームに対応した既定の出力先がコンテンツ「既定出力先」に定義されている必要があります。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
@ContentMapping(content = "既定出力先")
public final class DefaultOutputPathLoader implements Content<DefaultOutputPath> {

    /**
     * プログラム実行時のプラットフォーム要素
     */
    @Getter(AccessLevel.PRIVATE)
    private Platform platform;

    /**
     * デフォルトコンストラクタ
     */
    private DefaultOutputPathLoader() {
    }

    /**
     * コンストラクタ
     *
     * @param platform プログラム実行時のプラットフォーム要素
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DefaultOutputPathLoader(@NonNull Platform platform) {
        this.platform = platform;
    }

    /**
     * 引数として与えられた {@code platform} をもとに {@link DefaultOutputPathLoader}
     * クラスの新しいインスタンスを生成し返却します。
     *
     * @param platform プログラム実行時のプラットフォーム
     * @return {@link DefaultOutputPathLoader} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @see Platform
     */
    public static Content<DefaultOutputPath> of(@NonNull Platform platform) {
        return new DefaultOutputPathLoader(platform);
    }

    /**
     * コンテンツ要素定数
     */
    private enum ContentAttribute implements Attribute {
        環境変数名, 出力先ディレクトリ;

        @Override
        public String getString() {
            return this.name();
        }
    }

    /**
     * コンテンツ条件定数
     */
    private enum ContentConditions implements Condition {
        プラットフォームコード;

        @Override
        public String getString() {
            return this.name();
        }
    }

    @Override
    public DefaultOutputPath execute() {

        final Map<String, String> content = loadContent(this.getClass()).get(0);

        return DefaultOutputPath.of(content.get(ContentAttribute.環境変数名.getString()),
                content.get(ContentAttribute.出力先ディレクトリ.getString()));
    }

    @Override
    public List<Attribute> getAttributes() {
        final List<Attribute> attributes = new ArrayList<>(2);
        attributes.add(ContentAttribute.環境変数名);
        attributes.add(ContentAttribute.出力先ディレクトリ);

        return attributes;
    }

    @Override
    public Map<Condition, String> getConditions() {
        final Map<Condition, String> conditions = new HashMap<>(1);
        conditions.put(ContentConditions.プラットフォームコード, String.valueOf(this.getPlatform().getCode()));

        return conditions;
    }
}