import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomValidateReportComponent } from './custom-validate-report.component';

describe('CustomValidateReportComponent', () => {
  let component: CustomValidateReportComponent;
  let fixture: ComponentFixture<CustomValidateReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomValidateReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomValidateReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
