import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JsonSchemaWriterComponent } from './json-schema-writer.component';

describe('JsonSchemaWriterComponent', () => {
  let component: JsonSchemaWriterComponent;
  let fixture: ComponentFixture<JsonSchemaWriterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ JsonSchemaWriterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(JsonSchemaWriterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
