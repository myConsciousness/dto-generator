/**
 * Project Name : Generator<br>
 * File Name : Description.java<br>
 * Encoding : UTF-8<br>
 * Creation Date : 2020/05/25<br>
 * <p>
 * Copyright © 2020 Kato Shinya. All rights reserved.
 * <p>
 * This source code or any portion thereof must not be<br>
 * reproduced or used in any manner whatsoever.
 */

package org.thinkit.generator.rule.factory.resource;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * プログラムリソースにおける説明を抽象化した抽象クラスです。<br>
 * この抽象クラスでは説明定義に必要な情報を定義します。
 * 
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 * @see Component
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class Description extends Component {

    /**
     * 説明
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private String description = "";

    /**
     * デフォルトコンストラクタ
     */
    @SuppressWarnings("unused")
    private Description() {
    }

    /**
     * コンストラクタ
     * 
     * @param description 説明
     * @exception NullPointerException 引数として{@code null}が渡された場合
     */
    public Description(String description) {
        this.description = description;
    }
}