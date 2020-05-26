/**
 * Project Name : Generator<br>
 * File Name : Process.java<br>
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
 * プログラムリソースにおける処理を抽象化した抽象クラスです。<br>
 * この抽象クラスでは処定義に必要な情報を保持します。
 * 
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 * @see Component
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class Process extends Component {

    /**
     * 変数名
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private String variableName = "";

    /**
     * デフォルトコンストラクタ
     */
    @SuppressWarnings("unused")
    private Process() {
    }

    /**
     * コンストラクタ
     * 
     * @param variableName 変数名
     * @exception NulLPointerException 引数として{@code null}が渡された場合
     */
    public Process(String variableName) {
        this.variableName = variableName;
    }
}