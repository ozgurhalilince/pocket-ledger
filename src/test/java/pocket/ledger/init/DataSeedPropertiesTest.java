package pocket.ledger.init;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DataSeedPropertiesTest {

  @Test
  void defaultValues_shouldBeSetCorrectly() {
    DataSeedProperties properties = new DataSeedProperties();

    assertThat(properties.isEnabled()).isFalse();
    assertThat(properties.getCount()).isEqualTo(25);
  }

  @Test
  void setEnabled_shouldUpdateEnabledFlag() {
    DataSeedProperties properties = new DataSeedProperties();

    properties.setEnabled(true);

    assertThat(properties.isEnabled()).isTrue();
  }

  @Test
  void setCount_shouldUpdateCount() {
    DataSeedProperties properties = new DataSeedProperties();

    properties.setCount(100);

    assertThat(properties.getCount()).isEqualTo(100);
  }

  @Test
  void setters_shouldAllowNegativeCount() {
    DataSeedProperties properties = new DataSeedProperties();

    properties.setCount(-1);

    assertThat(properties.getCount()).isEqualTo(-1);
  }

  @Test
  void setters_shouldAllowZeroCount() {
    DataSeedProperties properties = new DataSeedProperties();

    properties.setCount(0);

    assertThat(properties.getCount()).isEqualTo(0);
  }

  @Test
  void equals_shouldWorkCorrectly() {
    DataSeedProperties properties1 = new DataSeedProperties();
    properties1.setEnabled(true);
    properties1.setCount(50);

    DataSeedProperties properties2 = new DataSeedProperties();
    properties2.setEnabled(true);
    properties2.setCount(50);

    DataSeedProperties properties3 = new DataSeedProperties();
    properties3.setEnabled(false);
    properties3.setCount(25);

    assertThat(properties1).isEqualTo(properties2);
    assertThat(properties1).isNotEqualTo(properties3);
    assertThat(properties1).isNotEqualTo(null);
    assertThat(properties1).isNotEqualTo("not a DataSeedProperties");
    assertThat(properties1).isEqualTo(properties1);
  }

  @Test
  void hashCode_shouldBeConsistent() {
    DataSeedProperties properties1 = new DataSeedProperties();
    properties1.setEnabled(true);
    properties1.setCount(50);

    DataSeedProperties properties2 = new DataSeedProperties();
    properties2.setEnabled(true);
    properties2.setCount(50);

    assertThat(properties1.hashCode()).isEqualTo(properties2.hashCode());
    assertThat(properties1.hashCode()).isEqualTo(properties1.hashCode());
  }

  @Test
  void toString_shouldContainAllFields() {
    DataSeedProperties properties = new DataSeedProperties();
    properties.setEnabled(true);
    properties.setCount(100);

    String toString = properties.toString();

    assertThat(toString).contains("enabled");
    assertThat(toString).contains("count");
    assertThat(toString).contains("true");
    assertThat(toString).contains("100");
  }

  @Test
  void equals_shouldHandleDifferentEnabledValues() {
    DataSeedProperties properties1 = new DataSeedProperties();
    properties1.setEnabled(true);
    properties1.setCount(25);

    DataSeedProperties properties2 = new DataSeedProperties();
    properties2.setEnabled(false);
    properties2.setCount(25);

    assertThat(properties1).isNotEqualTo(properties2);
  }

  @Test
  void equals_shouldHandleDifferentCountValues() {
    DataSeedProperties properties1 = new DataSeedProperties();
    properties1.setEnabled(true);
    properties1.setCount(25);

    DataSeedProperties properties2 = new DataSeedProperties();
    properties2.setEnabled(true);
    properties2.setCount(50);

    assertThat(properties1).isNotEqualTo(properties2);
  }
}
