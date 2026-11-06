import type { Provider } from '@angular/core';
import { TestBed } from '@angular/core/testing';

type CompilerOptions = Partial<{
  providers: Provider[];
  useJit: boolean;
  preserveWhitespaces: boolean;
}>;
export type ConfigureFn = (testBed: typeof TestBed) => void;

export const configureTests = (
  configure: ConfigureFn,
  compilerOptions: CompilerOptions = {}
): Promise<void> => {
  const compilerConfig: CompilerOptions = {
    preserveWhitespaces: false,
    ...compilerOptions,
  };

  TestBed.configureCompiler(compilerConfig);

  configure(TestBed);

  return TestBed.compileComponents();
};
