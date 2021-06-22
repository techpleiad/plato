import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RulesSchemaComponent } from './rules-schema.component';

describe('RulesSchemaComponent', () => {
  let component: RulesSchemaComponent;
  let fixture: ComponentFixture<RulesSchemaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RulesSchemaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RulesSchemaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
