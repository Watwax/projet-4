import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';
import { routes } from './app.routes';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { ListComponent } from './pages/sessions/components/list/list.component';

describe('application routes', () => {
  it('redirects the root and protects the login and registration pages', () => {
    expect(routes[0]).toMatchObject({ path: '', pathMatch: 'full', redirectTo: 'login' });
    expect(routes.find(route => route.path === 'login')).toMatchObject({
      canActivate: [UnauthGuard],
      component: LoginComponent,
    });
    expect(routes.find(route => route.path === 'register')).toMatchObject({
      canActivate: [UnauthGuard],
      component: RegisterComponent,
    });
  });

  it('protects the session routes and provides a not-found fallback', () => {
    const sessionsRoute = routes.find(route => route.path === 'sessions');

    expect(sessionsRoute?.canActivate).toEqual([AuthGuard]);
    expect(sessionsRoute?.children?.[0].component).toBe(ListComponent);
    expect(routes.find(route => route.path === '404')?.component).toBe(NotFoundComponent);
    expect(routes[routes.length - 1]).toMatchObject({ path: '**', redirectTo: '404' });
    expect(routes.length).toBeGreaterThan(0);
  });
});
