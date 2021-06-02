import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewServiceComponent } from './add-new-service.component';

describe('AddNewServiceComponent', () => {
  let component: AddNewServiceComponent;
  let fixture: ComponentFixture<AddNewServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddNewServiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
