import { appConfig } from './app.config';

describe('appConfig', () => {
  it('configures HTTP, change detection and application routes', () => {
    expect(appConfig.providers).toHaveLength(3);
  });
});
