import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Teacher } from '../models/teacher.interface';
import { TeacherService } from './teacher.service';

describe('TeacherService integration', () => {
  let service: TeacherService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TeacherService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('loads all teachers', () => {
    const teachers: Teacher[] = [
      {
        id: 2,
        firstName: 'Taylor',
        lastName: 'Smith',
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-02'),
      },
    ];

    service.all().subscribe(response => expect(response).toEqual(teachers));

    const request = httpTestingController.expectOne('api/teacher');
    expect(request.request.method).toBe('GET');
    request.flush(teachers);
  });

  it('loads a teacher by id', () => {
    const teacher: Teacher = {
      id: 2,
      firstName: 'Taylor',
      lastName: 'Smith',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-02'),
    };

    service.detail('2').subscribe(response => expect(response).toEqual(teacher));

    const request = httpTestingController.expectOne('api/teacher/2');
    expect(request.request.method).toBe('GET');
    request.flush(teacher);
  });
});
