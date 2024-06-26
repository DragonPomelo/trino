/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.operator.annotations;

import io.trino.metadata.FunctionBinding;
import io.trino.spi.function.CatalogSchemaFunctionName;
import io.trino.spi.function.FunctionDependencies;
import io.trino.spi.function.FunctionDependencyDeclaration.FunctionDependencyDeclarationBuilder;
import io.trino.spi.function.InvocationConvention;
import io.trino.spi.function.ScalarFunctionImplementation;
import io.trino.spi.type.TypeSignature;

import java.util.List;
import java.util.Objects;

import static io.trino.metadata.SignatureBinder.applyBoundVariables;
import static java.util.Objects.requireNonNull;

public final class FunctionImplementationDependency
        extends ScalarImplementationDependency
{
    private final CatalogSchemaFunctionName name;
    private final List<TypeSignature> argumentTypes;

    public FunctionImplementationDependency(CatalogSchemaFunctionName name, List<TypeSignature> argumentTypes, InvocationConvention invocationConvention, Class<?> type)
    {
        super(invocationConvention, type);
        this.name = requireNonNull(name, "name is null");
        this.argumentTypes = requireNonNull(argumentTypes, "argumentTypes is null");
    }

    public CatalogSchemaFunctionName getName()
    {
        return name;
    }

    public List<TypeSignature> getArgumentTypes()
    {
        return argumentTypes;
    }

    @Override
    public void declareDependencies(FunctionDependencyDeclarationBuilder builder)
    {
        builder.addFunctionSignature(name, argumentTypes);
    }

    @Override
    protected ScalarFunctionImplementation getImplementation(FunctionBinding functionBinding, FunctionDependencies functionDependencies, InvocationConvention invocationConvention)
    {
        List<TypeSignature> types = applyBoundVariables(argumentTypes, functionBinding);
        return functionDependencies.getScalarFunctionImplementationSignature(name, types, invocationConvention);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionImplementationDependency that = (FunctionImplementationDependency) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(argumentTypes, that.argumentTypes);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, argumentTypes);
    }
}
