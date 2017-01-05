/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
define(['lodash', './node'], function (_, ASTNode) {

    var TypeName = function (type) {
        this.typename = type;
    };

    TypeName.prototype = Object.create(ASTNode.prototype);
    TypeName.prototype.constructor = TypeName;


    TypeName.prototype.setTypeName = function (typename) {
        if (!_.isNil(typename)) {
            this.typename = typename;
        }
    };

    TypeName.prototype.getType = function () {
        return this.typename;
    };

    /**
     * initialize from json
     * @param jsonNode
     */
    TypeName.prototype.initFromJson = function (jsonNode) {
        this.typename = jsonNode.type_name;
    };

    return TypeName;
});